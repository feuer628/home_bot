package com.cheremnov.bot;

import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.command.ICommandHandler;
import com.cheremnov.bot.command.IMessageHandler;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import com.cheremnov.bot.exception.BotBlockedException;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.Setter;
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


    private static final IMessageHandler defaultMessageHandler = (message, bot) -> bot.sendText(message.getChatId(), "Не понятно...");
    private final Map<String, ICallbackHandler> prefixCallbacks = new ConcurrentHashMap<>();
    private final Map<String, ICommandHandler> commandHandlers = new ConcurrentHashMap<>();

    @Autowired
    private TrustedUserRepository trustedUserRepository;

    @Autowired
    private Handlers handlers;

    @Setter
    private IMessageHandler messageHandler = defaultMessageHandler;


    @Autowired
    public Bot(@Value("${token}") String botToken) {
        super(botToken);
    }

    @PostConstruct
    public void initHandlers() {
        for (ICallbackHandler callbackHandler : handlers.getCallbackHandlers()) {
            prefixCallbacks.put(callbackHandler.callbackPrefix(), callbackHandler);
        }
        for (ICommandHandler commandHandler : handlers.getCommandHandlers()) {
            commandHandlers.put(commandHandler.getCommandName(), commandHandler);
        }
    }

    public void setMenuCommands(@NonNull Long userId) {
        boolean isTrustedUser = trustedUserRepository.existsById(userId);
        List<BotCommand> menuCommandList = new ArrayList<>();
        for (ICommandHandler commandHandler : commandHandlers.values()) {
            if (!commandHandler.isCommandHidden() && (commandHandler.isPublicCommand() || isTrustedUser)) {
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
        return "CheremnovBot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
            return;
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            setMenuCommands(update.getMessage().getFrom().getId());
            String text = update.getMessage().getText().trim();
            if (text.startsWith("/")) {
                String cmd = text.split(" ")[0].substring(1); // "start" from "/start args"
                ICommandHandler handler = commandHandlers.get(cmd);
                if (handler == null) {
                    sendText(update.getMessage().getChatId(), "Не найден обработчик для команды " + cmd);
                    log.error("Не найден обработчик для команды " + cmd);
                    return;
                }
                handler.handle(update.getMessage(), this);
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

    public void restoreDefaultMessageHandler() {
        this.messageHandler = defaultMessageHandler;
    }

}
