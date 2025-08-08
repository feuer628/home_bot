package com.cheremnov.bot;

import com.cheremnov.bot.command.AbstractCommand;
import com.cheremnov.bot.command.Commands;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.util.WebhookUtils;

import java.util.Objects;

public class HomeBot extends TelegramLongPollingBot {

    public HomeBot(String botToken) {
        super(botToken);
        try {
            // установка меню
            SetMyCommands menu = new SetMyCommands();
            menu.setCommands(Commands.getMenuCommands());
            execute(menu);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            String botToken = args[0];
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new HomeBot(botToken));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getBotUsername() {
        return "CheremnovBot";
    }


    public void onUpdateReceived(Update update) {

        String commandText;
        MaybeInaccessibleMessage message;
        Long userId;
        SendMessage sendMessage = new SendMessage();
        try {
            if (update.hasCallbackQuery()) {
                commandText = update.getCallbackQuery().getData();
                message = update.getCallbackQuery().getMessage();
                userId = update.getCallbackQuery().getFrom().getId();
            } else {
                commandText = update.getMessage().getText();
                message = update.getMessage();
                userId = update.getMessage().getFrom().getId();
            }
            sendMessage.setChatId(message.getChatId());

            AbstractCommand command;
            // проверяем нет ли какой-либо незавершенной цепочки команд
            boolean hasChainCommand = ChainCommandHandler.hasChainCommand(userId);
            if (hasChainCommand) {
                // если цепочки есть, берем следующую команду
                command = ChainCommandHandler.getChainUserCommand(userId, update);
            } else {
                // если цепочки нет, ищем новую команду
                command = Commands.getCommandForMessage(commandText);
            }

            if (command.checkRight(userId)) {
                command.parseAndCheckArgs();
                command.doAction(sendMessage);
                Class<? extends AbstractCommand> nextCommand = command.nextCommand();
                if (nextCommand == null) {
                    if (hasChainCommand) {
                        ChainCommandHandler.clearChainCommand(userId);
                    }
                } else {
                    ChainCommandHandler.setNextUserCommand(userId, nextCommand, command.getClass());
                }
                Objects.requireNonNull(sendMessage.getText(), "Не установлен текст сообщения для команды " + commandText);
            } else {
                sendMessage.setText("У вас нет прав для выполнения этого запроса");
            }

            execute(sendMessage);
        } catch (Exception e) {
            sendMessage.setText("Ошибка при выполнении запроса:\n" + e.getMessage());
            try {
                execute(sendMessage);
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}
