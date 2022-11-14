package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class UsersList extends AbstractCommand {
    @Override
    public void checkRight() {

    }

    @Override
    public SendMessage doActionAndGetMessage(Message message) {
        super.doActionAndGetMessage(message);
        return null;
    }

}
