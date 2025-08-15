package com.cheremnov.bot.command;

import com.cheremnov.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ICommandHandler {
    String getCommandName();
    String getCommandDescription();
    void handle(Message message, Bot bot);
}
