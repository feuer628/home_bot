package com.cheremnov.bot;

import com.cheremnov.bot.command.AbstractCommandHandler;
import com.cheremnov.bot.command.AbstractMessageHandler;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.exception.BotBlockedException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class Bot extends TelegramLongPollingBot {


    private static final AbstractMessageHandler defaultMessageHandler = new AbstractMessageHandler() {
        @Override
        public void handleMessage(Message message, Bot bot) {
            bot.sendText(message.getChatId(), "Ничего не понимаю...");
        }
    };
    private final Map<String, ICallbackHandler> prefixCallbacks = new ConcurrentHashMap<>();
    private final Map<String, AbstractCommandHandler> commandHandlers = new ConcurrentHashMap<>();

    @Autowired
    private Handlers handlers;

    private final Map<Long, AbstractMessageHandler> messageHandlers = new ConcurrentHashMap<>();


    @Autowired
    public Bot(@Value("${token}") String botToken) {
        super(botToken);
    }

    @PostConstruct
    public void initBot() {
        for (ICallbackHandler callbackHandler : handlers.getCallbackHandlers()) {
            prefixCallbacks.put(callbackHandler.callbackPrefix(), callbackHandler);
        }
        for (AbstractCommandHandler commandHandler : handlers.getCommandHandlers()) {
            commandHandlers.put(commandHandler.getCommandName(), commandHandler);
        }
        setMenuCommands();
    }

    private void setMenuCommands() {
        List<BotCommand> menuCommandList = new ArrayList<>();
        for (AbstractCommandHandler commandHandler : commandHandlers.values()) {
            if (!commandHandler.isCommandHidden()) {
                menuCommandList.add(new BotCommand(commandHandler.getCommandName(), commandHandler.getCommandDescription()));
            }
        }
        if (menuCommandList.isEmpty()) {
            return;
        }
        SetMyCommands menu = new SetMyCommands();
        menu.setCommands(menuCommandList);
        try {
            execute(menu);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBotUsername() {
        return "Тонус Арт";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
            return;
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText().trim();
            if (text.startsWith("/")) {
                String cmd = text.split(" ")[0].substring(1); // "start" from "/start args"
                AbstractCommandHandler handler = commandHandlers.get(cmd);
                if (handler == null) {
                    sendText(update.getMessage().getChatId(), "Не найден обработчик для команды " + cmd);
                    log.error("Не найден обработчик для команды " + cmd);
                    return;
                }
                handler.handle(update.getMessage(), this);
            } else {
                AbstractMessageHandler messageHandler = messageHandlers.getOrDefault(update.getMessage().getChatId(), defaultMessageHandler);
                messageHandler.handle(update.getMessage(), this);
            }
        }
    }

    private void handleCallback(CallbackQuery cq) {
        String data = cq.getData(); // например "confirm:123"
        // 1) точный префикс (до ":" или весь)
        String key = data.contains(":") ? data.substring(0, data.indexOf(':')) : data;

        ICallbackHandler handler = prefixCallbacks.get(key);
        if (handler != null) {
            handler.handle(cq, this);
            return;
        }

        // 3) дефолтное поведение
        answerCallback(cq, "Неизвестное действие");
    }

    public void answerCallback(CallbackQuery cq) {
        answerCallback(cq, null);
    }

    // Утилиты для отправки ответов
    public void answerCallback(CallbackQuery cq, String text) {
        AnswerCallbackQuery ans = new AnswerCallbackQuery();
        ans.setCallbackQueryId(cq.getId());
        ans.setText(text);
        ans.setShowAlert(true);
        try {
            execute(ans);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteInlineMarkup(MaybeInaccessibleMessage message) {
        setInlineMarkup(message, null);
    }

    public void setInlineMarkup(MaybeInaccessibleMessage message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
        editMarkup.setChatId(message.getChatId());
        editMarkup.setMessageId(message.getMessageId());
        editMarkup.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(editMarkup);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void editMessageText(Long chatId, Integer messageId, String text) {
        editMessageText(chatId, messageId, text, null);
    }

    public void editMessageText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(chatId.toString());
        edit.setMessageId(messageId);
        edit.setText(text);
        edit.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(edit);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendText(Long chatId, String text) {
        sendText(chatId, text, null);
    }

    public void sendText(Long chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage m = new SendMessage();
        m.setChatId(chatId.toString());
        m.setText(text);
        m.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(m);
        } catch (TelegramApiException e) {
            if (e.getMessage().contains("bot was blocked by the user")) {
                throw new BotBlockedException(e);
            }
            throw new RuntimeException(e);
        }
    }

    public void setMessageHandler(long chatId, AbstractMessageHandler messageHandler) {
        messageHandlers.put(chatId, messageHandler);
    }

    public void deleteMessageHandler(long chatId) {
        messageHandlers.remove(chatId);
    }

}
