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

public class HomeBot extends TelegramLongPollingBot {

    public HomeBot() {
        try {
            this.execute(new SetMyCommands(Commands.getMenuCommands(), new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new HomeBot());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return "CheremnovHomeBot";
        //return "tetriqwiexjvcs_bot";
    }

    public String getBotToken() {
        return "";
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
