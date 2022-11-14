package com.cheremnov.bot;

import com.cheremnov.bot.command.AbstractCommand;
import com.cheremnov.bot.command.Commands;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class HomeBot extends TelegramLongPollingBot {

    private final Properties config = new Properties();

    public HomeBot(String configPath) {
        try(FileReader fileReader = new FileReader(configPath)) {
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
        AbstractCommand commandClass = Commands.getCommandForMessage(update.getMessage().getText());
        commandClass.checkRight();
        SendMessage replyMessage = commandClass.doActionAndGetMessage(update.getMessage());
        try {
            execute(replyMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
