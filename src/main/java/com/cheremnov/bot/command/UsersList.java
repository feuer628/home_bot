package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class UsersList extends AbstractCommand {
    public static final String COMMAND_NAME = "list_user";

    public UsersList(List<String> args) {
        super(COMMAND_NAME, args);
    }

    @Override
    public void doAction(SendMessage message) {
        super.doAction(message);
    }

}
