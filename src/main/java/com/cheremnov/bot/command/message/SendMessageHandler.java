package com.cheremnov.bot.command.message;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.command.AbstractMessageHandler;
import com.cheremnov.bot.messages.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendMessageHandler extends AbstractMessageHandler {

    @Autowired
    private MessageSender messageSender;

    @Override
    public boolean handleMessage(MsgMessage message, Messenger messenger) {
        if (!message.hasText()) {
            messenger.sendText(message.getChatId(), "Пока можно отправлять только текст...");
            return false;
        }
        messageSender.sendAllSubscribers(message.getText());
        return true;
    }
}