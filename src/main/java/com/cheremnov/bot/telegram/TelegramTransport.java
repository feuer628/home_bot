package com.cheremnov.bot.telegram;

import com.cheremnov.bot.messenger.KeyButton;
import com.cheremnov.bot.messenger.Keyboard;
import com.cheremnov.bot.messenger.MenuCommand;
import com.cheremnov.bot.messenger.MessageTransport;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.messenger.MsgUser;
import com.cheremnov.bot.messenger.Platform;
import com.cheremnov.bot.messenger.TransportListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Транспорт Telegram: приём обновлений long polling'ом telegrambots
 * и исходящие запросы Bot API.
 */
@Slf4j
@Component
public class TelegramTransport extends TelegramLongPollingBot implements MessageTransport {

    private final boolean enabled;

    private TransportListener listener;

    public TelegramTransport(DefaultBotOptions options,
                             @Value("${token:}") String botToken,
                             @Value("${telegram.enabled:true}") boolean enabled) {
        super(options, botToken);
        this.enabled = enabled;
    }

    @Override
    public Platform platform() {
        return Platform.TELEGRAM;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setListener(TransportListener listener) {
        this.listener = listener;
    }

    public String getBotUsername() {
        return "CheremnovBot";
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerBot() {
        if (!isEnabled()) {
            log.info("Транспорт Telegram отключён (telegram.enabled=false)");
            return;
        }
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
            log.info("Транспорт Telegram запущен");
        } catch (Exception e) {
            log.error("Ошибка при регистрации Telegram-бота", e);
        }
    }

    // ------------------------------------------------------------------
    // Входящие обновления
    // ------------------------------------------------------------------

    @Override
    public void onUpdateReceived(Update update) {
        if (listener == null) {
            return;
        }
        if (update.hasCallbackQuery()) {
            listener.onCallback(toMsgCallback(update.getCallbackQuery()));
        } else if (update.hasMessage()) {
            listener.onMessage(toMsgMessage(update.getMessage()));
        }
    }

    private MsgMessage toMsgMessage(Message message) {
        return MsgMessage.builder()
                .chatId(message.getChatId() == null ? null : Platform.TELEGRAM.chatKey(message.getChatId()))
                .messageId(String.valueOf(message.getMessageId()))
                .text(message.hasText() ? message.getText() : null)
                .from(toMsgUser(message.getFrom()))
                .forwardedFrom(toMsgUser(message.getForwardFrom()))
                .build();
    }

    private MsgCallback toMsgCallback(CallbackQuery callback) {
        MaybeInaccessibleMessage message = callback.getMessage();
        MsgMessage msgMessage = MsgMessage.builder().build();
        if (message != null) {
            msgMessage.setChatId(message.getChatId() == null
                    ? null : Platform.TELEGRAM.chatKey(message.getChatId()));
            msgMessage.setMessageId(String.valueOf(message.getMessageId()));
            if (message instanceof Message fullMessage) {
                msgMessage.setText(fullMessage.hasText() ? fullMessage.getText() : null);
            }
        }
        return MsgCallback.builder()
                .id(callback.getId())
                .data(callback.getData())
                .from(toMsgUser(callback.getFrom()))
                .message(msgMessage)
                .build();
    }

    private MsgUser toMsgUser(User user) {
        if (user == null) {
            return null;
        }
        return MsgUser.builder()
                .id(Platform.TELEGRAM.userKey(user.getId()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bot(user.getIsBot())
                .build();
    }

    // ------------------------------------------------------------------
    // Исходящие запросы
    // ------------------------------------------------------------------

    @Override
    public void sendText(String chatKey, String text, Keyboard keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(rawChatId(chatKey));
        message.setText(text);
        message.setReplyMarkup(toTelegramKeyboard(keyboard));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            if (e.getMessage() != null && e.getMessage().contains("bot was blocked by the user")) {
                log.warn("Попытка отправить сообщение в заблокированный чат: {}.", chatKey);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void editMessageText(String chatKey, String messageId, String text, Keyboard keyboard) {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(rawChatId(chatKey));
        edit.setMessageId(Integer.parseInt(messageId));
        edit.setText(text);
        edit.setReplyMarkup(toTelegramKeyboard(keyboard));
        try {
            execute(edit);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setInlineMarkup(String chatKey, String messageId, Keyboard keyboard) {
        EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
        editMarkup.setChatId(rawChatId(chatKey));
        editMarkup.setMessageId(Integer.parseInt(messageId));
        editMarkup.setReplyMarkup(toTelegramKeyboard(keyboard));
        try {
            execute(editMarkup);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void answerCallback(String callbackId, String text) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackId);
        answer.setText(text);
        answer.setShowAlert(true);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setCommands(List<MenuCommand> commands) {
        List<BotCommand> menuCommandList = new ArrayList<>();
        for (MenuCommand command : commands) {
            menuCommandList.add(new BotCommand(command.getName(), command.getDescription()));
        }
        SetMyCommands menu = new SetMyCommands();
        menu.setCommands(menuCommandList);
        try {
            execute(menu);
        } catch (TelegramApiException e) {
            log.error("Ошибка установки меню", e);
        }
    }

    // ------------------------------------------------------------------

    private String rawChatId(String chatKey) {
        return Platform.rawId(chatKey);
    }

    private InlineKeyboardMarkup toTelegramKeyboard(Keyboard keyboard) {
        if (keyboard == null || keyboard.getRows() == null || keyboard.getRows().isEmpty()) {
            return null;
        }
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (List<KeyButton> row : keyboard.getRows()) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (KeyButton button : row) {
                InlineKeyboardButton tgButton = new InlineKeyboardButton(button.getText());
                if (button.isLink()) {
                    tgButton.setUrl(button.getUrl());
                } else {
                    tgButton.setCallbackData(button.getCallbackData());
                }
                buttons.add(tgButton);
            }
            rows.add(buttons);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }
}