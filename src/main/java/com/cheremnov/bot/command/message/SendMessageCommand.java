package com.cheremnov.bot.command.message;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;

@Component
public class SendMessageCommand extends AbstractCommandHandler {

    @Override
    public String getCommandName() {
        return "message";
    }

    @Override
    public String getCommandDescription() {
        return "Отправка сообщения всем подписчикам";
    }

    @Override
    public void handleCommand(MsgMessage message, Messenger messenger) {
        messenger.sendText(message.getChatId(), "Напишите в этот чат сообщение, которое нужно отправить всем подписчикам");
        messenger.setMessageHandler(message.getChatId(), getBean(SendMessageHandler.class));
    }
}