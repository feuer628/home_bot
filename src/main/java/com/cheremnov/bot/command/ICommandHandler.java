package com.cheremnov.bot.command;

import com.cheremnov.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface ICommandHandler {

    default boolean isCommandHidden() {
        return false;
    }

    default boolean isPublicCommand() {
        return false;
    }

    String getCommandName();

    default String getCommandDescription() {
        return null;
    }

    void handle(Message message, Bot bot);

}
