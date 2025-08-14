package com.cheremnov.bot;

import com.cheremnov.bot.callback.ICallbackHandler;
import com.cheremnov.bot.command.ICommandHandler;
import com.cheremnov.bot.message.IMessageHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Bot extends TelegramLongPollingBot {


    private static final IMessageHandler defaultMessageHandler = (message, bot) -> bot.sendText(message.getChatId(), "Не понятно...");
    private final Map<String, ICallbackHandler> prefixCallbacks = new ConcurrentHashMap<>();
    private final Map<String, ICommandHandler> commandHandlers = new ConcurrentHashMap<>();
    @Setter
    private IMessageHandler messageHandler = defaultMessageHandler;

    public Bot(String botToken) {
        super(botToken);
    }

    public void initHandlers(Handlers handlers) {
        for (ICallbackHandler callbackHandler : handlers.getCallbackHandlers()) {
            prefixCallbacks.put(callbackHandler.callbackPrefix(), callbackHandler);
        }
        for (ICommandHandler commandHandler : handlers.getCommandHandlers()) {
            commandHandlers.put(commandHandler.getCommandName(), commandHandler);
        }
        // установка меню
        SetMyCommands menu = new SetMyCommands();
        menu.setCommands(getMenuCommands(handlers.getCommandHandlers()));
        try {
            execute(menu);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<BotCommand> getMenuCommands(List<ICommandHandler> commandHandlers) {
        List<BotCommand> menuCommandList = new ArrayList<>();
        for (ICommandHandler commandHandler : commandHandlers) {
            menuCommandList.add(new BotCommand(commandHandler.getCommandName(), commandHandler.getCommandDescription()));
        }
        return menuCommandList;
    }

    public String getBotUsername() {
        return "CheremnovBot";
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
                ICommandHandler handler = commandHandlers.get(cmd);
                if (handler == null) {
                    sendText(update.getMessage().getChatId(), "Не найден обработчик для команды " + cmd);
                    log.error("Не найден обработчик для команды " + cmd);
                    return;
                }
                try {
                    handler.handle(update.getMessage(), this);
                } catch (TelegramApiException e) {
                    log.error("Ошибка при обработке команды", e);
                    throw new RuntimeException(e);
                }
            } else {
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
        EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
        editMarkup.setChatId(message.getChatId());
        editMarkup.setMessageId(message.getMessageId());
        editMarkup.setReplyMarkup(null); // удаляем клавиатуру
        try {
            execute(editMarkup);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void editMessageText(Long chatId, Integer messageId, String text) {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(chatId.toString());
        edit.setMessageId(messageId);
        edit.setText(text);
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
            throw new RuntimeException(e);
        }
    }

    public void restoreDefaultMessageHandler() {
        this.messageHandler = defaultMessageHandler;
    }

}
