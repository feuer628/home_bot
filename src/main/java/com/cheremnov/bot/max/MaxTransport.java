package com.cheremnov.bot.max;

import com.cheremnov.bot.messenger.Keyboard;
import com.cheremnov.bot.messenger.Keys;
import com.cheremnov.bot.messenger.MenuCommand;
import com.cheremnov.bot.messenger.MessageTransport;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.messenger.MsgUser;
import com.cheremnov.bot.messenger.Platform;
import com.cheremnov.bot.messenger.TransportListener;
import com.cheremnov.bot.max.dto.MaxCallback;
import com.cheremnov.bot.max.dto.MaxLinkedMessage;
import com.cheremnov.bot.max.dto.MaxMessage;
import com.cheremnov.bot.max.dto.MaxRecipient;
import com.cheremnov.bot.max.dto.MaxUpdate;
import com.cheremnov.bot.max.dto.MaxUpdateList;
import com.cheremnov.bot.max.dto.MaxUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.OkHttpClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Транспорт Макса: long polling обновлений (GET /updates) и исходящие запросы API.
 */
@Slf4j
@Component
public class MaxTransport implements MessageTransport {

    private static final List<String> EVENT_TYPES =
            List.of("message_created", "message_callback", "message_edited", "bot_started");

    private final boolean enabled;
    private final String token;
    private final String baseUrl;
    private final int pollTimeoutSec;

    private MaxApi api;
    private TransportListener listener;
    private volatile boolean running;
    /** Ключ собственного пользователя бота ("mx:<id>"), определяется через GET /me. */
    private volatile String selfUserKey;

    /**
     * mid «слота команд» — служебного сообщения, которое виджет перезаписывает
     * командой ("/widget ..."), а бот опрашивает его содержимое.
     * Пустое значение — опрос слота выключен.
     */
    @Value("${max.widget-slot-mid:}")
    private String widgetSlotMid;

    /** Период опроса слота команд, мс. */
    @Value("${max.widget-slot-poll-ms:1500}")
    private long widgetSlotPollMs;

    private final java.util.concurrent.ScheduledExecutorService slotScheduler =
            java.util.concurrent.Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "max-widget-slot");
                thread.setDaemon(true);
                return thread;
            });

    public MaxTransport(@Value("${max.enabled:false}") boolean enabled,
                        @Value("${max.token:}") String token,
                        @Value("${max.base-url:https://platform-api2.max.ru}") String baseUrl,
                        @Value("${max.poll-timeout:30}") int pollTimeoutSec) {
        this.enabled = enabled;
        this.token = token;
        this.baseUrl = baseUrl;
        this.pollTimeoutSec = pollTimeoutSec;
    }

    @PostConstruct
    public void init() {
        if (!isEnabled()) {
            log.info("Транспорт Макса отключён (max.enabled=false или не задан max.token)");
            return;
        }
        OkHttpClient client = new OkHttpClient();
        client.setSslSocketFactory(MaxSsl.createSocketFactory());
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        // таймаут чтения должен превышать таймаут долгого опроса
        client.setReadTimeout(pollTimeoutSec + 70L, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        api = new MaxApi(baseUrl, token, client);
    }

    @Override
    public Platform platform() {
        return Platform.MAX;
    }

    @Override
    public boolean isEnabled() {
        return enabled && token != null && !token.isBlank();
    }

    @Override
    public void setListener(TransportListener listener) {
        this.listener = listener;
    }

    @Override
    public String selfUserKey() {
        return selfUserKey;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startPolling() {
        if (!isEnabled()) {
            return;
        }
        running = true;
        Thread thread = new Thread(this::pollLoop, "max-long-polling");
        thread.setDaemon(true);
        thread.start();
        log.info("Транспорт Макса запущен (long polling, timeout={}с)", pollTimeoutSec);
        if (widgetSlotMid != null && !widgetSlotMid.isBlank()) {
            slotScheduler.scheduleWithFixedDelay(this::pollWidgetSlot,
                    widgetSlotPollMs, widgetSlotPollMs, TimeUnit.MILLISECONDS);
            log.info("Слот команд виджета опрашивается каждые {} мс: {}", widgetSlotPollMs, widgetSlotMid.trim());
        }
    }

    /**
     * Опрос «слота команд»: виджет перезаписывает текст слота командой
     * (PUT /messages — это разрешено держателю токена), бот её выполняет.
     * Событй на такие действия Макс не шлёт, поэтому используется опрос.
     */
    private void pollWidgetSlot() {
        if (listener == null) {
            return;
        }
        String slotMid = widgetSlotMid.trim();
        try {
            MaxMessage slot = api.getMessage(slotMid);
            String text = slot.getBody() == null ? null : slot.getBody().getText();
            if (text == null || !text.trim().startsWith("/")) {
                return;
            }
            String command = text.trim();
            log.info("Команда из слота виджета: {}", command);
            MsgMessage message = toMsgMessage(slot);
            try {
                listener.onMessage(message);
            } finally {
                // сбрасываем слот, чтобы команда не выполнилась повторно
                try {
                    api.editMessageText(slotMid,
                            "Последняя команда: " + command
                                    + "\nОбработана в " + java.time.LocalTime.now().withNano(0), null);
                } catch (Exception e) {
                    log.warn("Не удалось сбросить слот команд: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Ошибка опроса слота команд: {}", e.getMessage());
        }
    }

    private void pollLoop() {
        refreshSelfUserKey();
        Long marker = null;
        while (running) {
            try {
                MaxUpdateList updateList = api.getUpdates(marker, pollTimeoutSec, EVENT_TYPES);
                if (updateList.getUpdates() != null) {
                    for (MaxUpdate update : updateList.getUpdates()) {
                        try {
                            handleUpdate(update);
                        } catch (Exception e) {
                            log.error("Ошибка обработки события Макса {}", update.getUpdate_type(), e);
                        }
                    }
                }
                if (updateList.getMarker() != null) {
                    marker = updateList.getMarker();
                }
            } catch (Exception e) {
                log.error("Ошибка long polling Макса, повтор через 5 сек: {}", e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    /**
     * Определяет собственный user_id бота (GET /me) — нужен для распознавания
     * служебных команд, отправленных самим ботом (например, виджетом).
     */
    private void refreshSelfUserKey() {
        try {
            JsonNode me = api.getMe();
            JsonNode userId = me.get("user_id");
            if (userId != null && !userId.isNull()) {
                selfUserKey = Platform.MAX.userKey(userId.asLong());
                log.info("Собственный пользователь бота Макса: {}", selfUserKey);
            }
        } catch (Exception e) {
            log.warn("Не удалось получить информацию о боте (GET /me): {}", e.getMessage());
        }
    }

    private void handleUpdate(MaxUpdate update) {
        if (update == null || update.getUpdate_type() == null || listener == null) {
            return;
        }
        switch (update.getUpdate_type()) {
            case "message_created", "message_edited" -> {
                MsgMessage message = toMsgMessage(update.getMessage());
                if (message == null) {
                    // Особенность long polling Макса: медиа-сообщения (например,
                    // голосовые) приходят без тела и контент не дозапрашивается.
                    // Полные события приходят только через Webhook.
                    log.info("Событие {} без тела сообщения — медиа-сообщение пропущено", update.getUpdate_type());
                    return;
                }
                if ("message_edited".equals(update.getUpdate_type()) && !message.isVoice()) {
                    log.info("Пропущено редактирование сообщения {}", message.getMessageId());
                    return;
                }
                enrichVoiceTranscription(message);
                listener.onMessage(message);
            }
            case "message_callback" -> {
                MsgCallback callback = toMsgCallback(update);
                if (callback != null) {
                    listener.onCallback(callback);
                }
            }
            case "bot_started" -> {
                // Нажатие "Начать" в Максе не приходит сообщением /start —
                // эмулируем его для паритета с Telegram
                MaxUser user = update.getUser();
                if (user != null && user.getUser_id() != null) {
                    listener.onMessage(MsgMessage.builder()
                            .chatId(Keys.maxUserChatKey(user.getUser_id()))
                            .text("/start")
                            .from(toMsgUser(user))
                            .build());
                }
            }
            default -> log.debug("Пропущено событие Макса: {}", update.getUpdate_type());
        }
    }

    // ------------------------------------------------------------------
    // Исходящие запросы
    // ------------------------------------------------------------------

    @Override
    public void sendText(String chatKey, String text, Keyboard keyboard) {
        api.sendMessage(chatKey, text, keyboard);
    }

    @Override
    public void editMessageText(String chatKey, String messageId, String text, Keyboard keyboard) {
        api.editMessageText(messageId, text, keyboard);
    }

    @Override
    public void setInlineMarkup(String chatKey, String messageId, Keyboard keyboard) {
        api.setInlineMarkup(messageId, keyboard);
    }

    @Override
    public void answerCallback(String callbackId, String text) {
        api.answerCallback(callbackId, text);
    }

    @Override
    public void setCommands(List<MenuCommand> commands) {
        api.setCommands(commands);
    }

    // ------------------------------------------------------------------
    // Конвертация
    // ------------------------------------------------------------------

    private MsgCallback toMsgCallback(MaxUpdate update) {
        MaxCallback callback = update.getCallback();
        if (callback == null) {
            return null;
        }
        MsgMessage message = toMsgMessage(update.getMessage());
        if (message == null) {
            message = MsgMessage.builder().build();
        }
        if (message.getChatId() == null && callback.getUser() != null && callback.getUser().getUser_id() != null) {
            message.setChatId(Keys.maxUserChatKey(callback.getUser().getUser_id()));
        }
        return MsgCallback.builder()
                .id(callback.getCallback_id())
                .data(callback.getPayload())
                .from(toMsgUser(callback.getUser()))
                .message(message)
                .build();
    }

    private MsgMessage toMsgMessage(MaxMessage message) {
        if (message == null) {
            return null;
        }
        MsgUser forwardedFrom = null;
        MaxLinkedMessage link = message.getLink();
        if (link != null && "forward".equals(link.getType())) {
            forwardedFrom = toMsgUser(link.getSender());
        }
        MsgMessage msgMessage = MsgMessage.builder()
                .chatId(chatKeyOf(message))
                .messageId(message.getBody() != null ? message.getBody().getMid() : null)
                .text(message.getBody() != null ? message.getBody().getText() : null)
                .from(toMsgUser(message.getSender()))
                .forwardedFrom(forwardedFrom)
                .build();
        extractAudio(message, msgMessage);
        return msgMessage;
    }

    /**
     * Достаёт из вложений аудио (голосовое): URL и транскрипцию.
     */
    private void extractAudio(MaxMessage message, MsgMessage msgMessage) {
        if (message.getBody() == null || message.getBody().getAttachments() == null
                || !message.getBody().getAttachments().isArray()) {
            return;
        }
        for (JsonNode attachment : message.getBody().getAttachments()) {
            String type = attachment.path("type").asText(null);
            if (!"audio".equals(type)) {
                continue;
            }
            msgMessage.setAudioUrl(attachment.path("payload").path("url").asText(null));
            String transcription = attachment.path("transcription").asText(null);
            if (transcription != null && !transcription.isBlank()) {
                msgMessage.setAudioTranscription(transcription);
            }
            log.debug("Голосовое сообщение: url={}, транскрипция: {}",
                    msgMessage.getAudioUrl(),
                    msgMessage.getAudioTranscription() == null ? "<нет>" : msgMessage.getAudioTranscription());
        }
    }

    /**
     * Макс распознаёт голосовые с задержкой — дозапрашиваем сообщение,
     * пока не появится транскрипция (максимум 3 попытки по 3 сек).
     */
    private void enrichVoiceTranscription(MsgMessage msgMessage) {
        if (!msgMessage.isVoice() || (msgMessage.getAudioTranscription() != null
                && !msgMessage.getAudioTranscription().isBlank()) || msgMessage.getMessageId() == null) {
            return;
        }
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            try {
                MaxMessage fresh = api.getMessage(msgMessage.getMessageId());
                if (fresh != null) {
                    extractAudio(fresh, msgMessage);
                    if (msgMessage.getAudioTranscription() != null
                            && !msgMessage.getAudioTranscription().isBlank()) {
                        return;
                    }
                }
            } catch (Exception e) {
                log.debug("Не удалось дозапросить транскрипцию: {}", e.getMessage());
            }
        }
    }

    /**
     * Ключ чата: для групп/каналов — "mx:c&lt;chatId&gt;", для диалога — "mx:u&lt;userId&gt;" собеседника.
     */
    private String chatKeyOf(MaxMessage message) {
        MaxRecipient recipient = message.getRecipient();
        if (recipient != null && recipient.getChat_id() != null) {
            return Keys.maxChatKey(recipient.getChat_id());
        }
        MaxUser sender = message.getSender();
        if (sender != null && sender.getUser_id() != null && !Boolean.TRUE.equals(sender.getIs_bot())) {
            return Keys.maxUserChatKey(sender.getUser_id());
        }
        if (recipient != null && recipient.getUser_id() != null) {
            return Keys.maxUserChatKey(recipient.getUser_id());
        }
        return null;
    }

    private MsgUser toMsgUser(MaxUser user) {
        if (user == null) {
            return null;
        }
        return MsgUser.builder()
                .id(user.getUser_id() == null ? null : Platform.MAX.userKey(user.getUser_id()))
                .firstName(user.getFirst_name())
                .lastName(user.getLast_name())
                .bot(Boolean.TRUE.equals(user.getIs_bot()))
                .build();
    }
}