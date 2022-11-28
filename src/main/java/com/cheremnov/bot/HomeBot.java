package com.cheremnov.bot;

import com.cheremnov.bot.command.AbstractCommand;
import com.cheremnov.bot.command.Commands;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.util.WebhookUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class HomeBot extends TelegramLongPollingBot {

    private final Properties config = new Properties();

    @Override
    public void clearWebhook() throws TelegramApiRequestException {
        WebhookUtils.clearWebhook(this);
    }

    public HomeBot(String configPath) {
        try (FileReader fileReader = new FileReader(configPath)) {
            config.load(fileReader);
            // установка меню
            SetMyCommands menu = new SetMyCommands();
            menu.setCommands(Commands.getMenuCommands());
            execute(menu);
        } catch (TelegramApiException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new HomeBot(args[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return config.getProperty("botUsername");
    }

    public String getBotToken() {
        return config.getProperty("botToken");
    }

    public void onUpdateReceived(Update update) {
        try {
            String commandText;
            Message message;
            Long userId;
            if (update.hasCallbackQuery()) {
                commandText = update.getCallbackQuery().getData();
                message = update.getCallbackQuery().getMessage();
                userId = update.getCallbackQuery().getFrom().getId();
            } else {
                commandText = update.getMessage().getText();
                message = update.getMessage();
                userId = update.getMessage().getFrom().getId();
            }

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

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            if (command.checkRight(userId)) {
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
            e.printStackTrace();
        }

    }
}
