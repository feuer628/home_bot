package com.cheremnov.bot;

import com.cheremnov.bot.command.AbstractCommand;
import com.cheremnov.bot.command.Commands;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class HomeBot extends TelegramLongPollingBot {

    private final Properties config = new Properties();

    public HomeBot(String configPath) {
        try (FileReader fileReader = new FileReader(configPath)) {
            config.load(fileReader);
            this.execute(new SetMyCommands(Commands.getMenuCommands(), new BotCommandScopeDefault(), null));
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
            if (update.hasCallbackQuery()) {
                commandText = update.getCallbackQuery().getData();
                message = update.getCallbackQuery().getMessage();
            } else {
                commandText = update.getMessage().getText();
                message = update.getMessage();
            }


            AbstractCommand commandClass = Commands.getCommandForMessage(commandText);
            commandClass.checkRight();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            commandClass.doAction(sendMessage);
            Objects.requireNonNull(sendMessage.getText(), "Ќе установлен текст сообщени€ дл€ команды " + commandText);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
