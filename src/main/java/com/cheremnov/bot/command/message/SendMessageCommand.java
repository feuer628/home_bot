package com.cheremnov.bot.command.message;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SendMessageCommand extends AbstractCommandHandler {

    public boolean isCommandHidden() {
        return true;
    }

    @Override
    public String getCommandName() {
        return "message";
    }

    @Override
    public String getCommandDescription() {
        return "Отправка сообщения всем подписчикам";
    }

    @Override
    public void handleCommand(Message message, Bot bot) {
        bot.sendText(message.getChatId(), "Напишите в этот чат сообщение, которое нужно отправить всем подписчикам");
        bot.setMessageHandler(message.getChatId(), getBean(SendMessageHandler.class));
    }
}
