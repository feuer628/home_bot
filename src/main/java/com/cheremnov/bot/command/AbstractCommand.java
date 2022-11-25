package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCommand {

    List<Long> trustedUsers = new LinkedList<>();

    long SUPER_ADMIN_ID = 410563696;
    protected final List<String> args;

    public AbstractCommand(List<String> args) {

        this.args = args;
    }

    public void checkRight() {

    }

    public void doAction(SendMessage message) {
        message.setText(getMessageText());
    }

    protected String getMessageText() {
        return "?????????????";
    }
}
