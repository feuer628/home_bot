package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class Unknown extends AbstractCommand {
    @Override
    public void checkRight() {

    }

    @Override
    public SendMessage doActionAndGetMessage(Message message) {
        return new SendMessage(message.getChatId().toString(), "Неизвестная команда");
    }
}
