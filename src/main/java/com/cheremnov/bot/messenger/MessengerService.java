package com.cheremnov.bot.messenger;

import com.cheremnov.bot.Handlers;
import com.cheremnov.bot.command.AbstractCommandHandler;
import com.cheremnov.bot.command.AbstractMessageHandler;
import com.cheremnov.bot.command.ICallbackHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Платформо-независимое ядро бота: маршрутизация входящих событий
 * (команды, callback'и, диалоговые сообщения) и исходящих сообщений
 * по префиксу ключа чата.
 */
@Slf4j
@Service
public class MessengerService implements Messenger, TransportListener {

    private static final AbstractMessageHandler defaultMessageHandler = new AbstractMessageHandler() {
        @Override
        public boolean handleMessage(MsgMessage message, Messenger messenger) {
            messenger.sendText(message.getChatId(), "Для ознакомления с возможностями бота используйте команду /help");
            return false;
        }
    };

    private final Map<String, ICallbackHandler> prefixCallbacks = new ConcurrentHashMap<>();
    private final Map<String, AbstractCommandHandler> commandHandlers = new ConcurrentHashMap<>();
    private final Map<String, AbstractMessageHandler> messageHandlers = new ConcurrentHashMap<>();
    private final Map<Platform, MessageTransport> transports = new ConcurrentHashMap<>();

    @Autowired
    private Handlers handlers;

    @Autowired
    private List<MessageTransport> transportList;

    @PostConstruct
    public void init() {
        for (MessageTransport transport : transportList) {
            if (transport.isEnabled()) {
                transports.put(transport.platform(), transport);
                transport.setListener(this);
            }
        }
        for (ICallbackHandler callbackHandler : handlers.getCallbackHandlers()) {
            prefixCallbacks.put(callbackHandler.callbackPrefix(), callbackHandler);
        }
        for (AbstractCommandHandler commandHandler : handlers.getCommandHandlers()) {
            commandHandlers.put(commandHandler.getCommandName(), commandHandler);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        List<MenuCommand> menuCommands = new ArrayList<>();
        for (AbstractCommandHandler commandHandler : commandHandlers.values()) {
            if (!commandHandler.isCommandHidden() && commandHandler.getCommandDescription() != null) {
                menuCommands.add(new MenuCommand(commandHandler.getCommandName(), commandHandler.getCommandDescription()));
            }
        }
        if (menuCommands.isEmpty()) {
            return;
        }
        for (MessageTransport transport : transports.values()) {
            try {
                transport.setCommands(menuCommands);
                log.info("Платформа {}: установлено команд меню {}", transport.platform(), menuCommands.size());
            } catch (Exception e) {
                log.error("Ошибка установки меню команд для платформы {}", transport.platform(), e);
            }
        }
    }

    // ------------------------------------------------------------------
    // Входящие события
    // ------------------------------------------------------------------

    @Override
    public void onMessage(MsgMessage message) {
        String text = message.hasText() ? message.getText().trim() : "";
        if (message.getFrom() != null && message.getFrom().isBot()) {
            // Макс доставляет боту и его собственные сообщения.
            // Служебные команды самого бота (например, /widget от виджета) —
            // обрабатываем, всё остальное от ботов игнорируем,
            // иначе сообщение-эхо «съедает» диалоговый обработчик
            if (!text.startsWith("/")) {
                log.debug("Пропущено сообщение от бота: {}", text);
                return;
            }
            if (!isSelfBotMessage(message)) {
                log.warn("Пропущена команда от чужого бота: {}", text);
                return;
            }
        }
        if (text.startsWith("/")) {
            String cmd = text.split(" ")[0].substring(1).split("@")[0];
            AbstractCommandHandler handler = commandHandlers.get(cmd);
            if (handler == null) {
                sendText(message.getChatId(), "Не найден обработчик для команды " + cmd);
                log.error("Не найден обработчик для команды {}", cmd);
                return;
            }
            handler.handle(message, this);
        } else {
            AbstractMessageHandler messageHandler =
                    messageHandlers.getOrDefault(message.getChatId(), defaultMessageHandler);
            messageHandler.handle(message, this);
        }
    }

    private boolean isSelfBotMessage(MsgMessage message) {
        MessageTransport transport = transportFor(message.getChatId());
        String selfKey = transport == null ? null : transport.selfUserKey();
        return selfKey != null && message.getFrom() != null && selfKey.equals(message.getFrom().getId());
    }

    @Override
    public void onCallback(MsgCallback callback) {
        String data = callback.getData() == null ? "" : callback.getData();
        String key = data.contains(":") ? data.substring(0, data.indexOf(':')) : data;

        ICallbackHandler handler = prefixCallbacks.get(key);
        if (handler != null) {
            handler.handle(callback, this);
            return;
        }
        answerCallback(callback, "Неизвестное действие");
    }

    // ------------------------------------------------------------------
    // Реестр диалоговых обработчиков
    // ------------------------------------------------------------------

    @Override
    public void setMessageHandler(String chatKey, AbstractMessageHandler handler) {
        messageHandlers.put(chatKey, handler);
    }

    @Override
    public void deleteMessageHandler(String chatKey) {
        messageHandlers.remove(chatKey);
    }

    // ------------------------------------------------------------------
    // Исходящие сообщения (маршрутизация по платформе)
    // ------------------------------------------------------------------

    @Override
    public void sendText(String chatKey, String text) {
        sendText(chatKey, text, null);
    }

    @Override
    public void sendText(String chatKey, String text, Keyboard keyboard) {
        MessageTransport transport = transportFor(chatKey);
        if (transport != null) {
            transport.sendText(chatKey, text, keyboard);
        }
    }

    @Override
    public void editMessageText(String chatKey, String messageId, String text, Keyboard keyboard) {
        MessageTransport transport = transportFor(chatKey);
        if (transport != null) {
            transport.editMessageText(chatKey, messageId, text, keyboard);
        }
    }

    @Override
    public void setInlineMarkup(String chatKey, String messageId, Keyboard keyboard) {
        MessageTransport transport = transportFor(chatKey);
        if (transport != null) {
            transport.setInlineMarkup(chatKey, messageId, keyboard);
        }
    }

    @Override
    public void deleteInlineMarkup(String chatKey, String messageId) {
        setInlineMarkup(chatKey, messageId, null);
    }

    @Override
    public void answerCallback(MsgCallback callback) {
        answerCallback(callback, null);
    }

    @Override
    public void answerCallback(MsgCallback callback, String text) {
        if (callback == null || callback.getMessage() == null) {
            return;
        }
        MessageTransport transport = transportFor(callback.getMessage().getChatId());
        if (transport != null) {
            try {
                transport.answerCallback(callback.getId(), text);
            } catch (Exception e) {
                // ответ на callback — вспомогательная операция, не рвём обработку
                log.warn("Не удалось ответить на callback: {}", e.getMessage());
            }
        }
    }

    private MessageTransport transportFor(String chatKey) {
        Platform platform = Platform.fromKey(chatKey);
        if (platform == null) {
            log.warn("Неизвестный платформенный префикс ключа чата: {}", chatKey);
            return null;
        }
        MessageTransport transport = transports.get(platform);
        if (transport == null) {
            log.warn("Транспорт платформы {} отключён, сообщение в {} не отправлено", platform, chatKey);
        }
        return transport;
    }
}