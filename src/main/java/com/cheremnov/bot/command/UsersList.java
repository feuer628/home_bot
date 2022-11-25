package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class UsersList extends AbstractCommand {
    public UsersList(List<String> args) {
        super(args);
    }

    @Override
    public void checkRight() {

    }

    @Override
    public void doAction(SendMessage message) {
        super.doAction(message);
    }

}
