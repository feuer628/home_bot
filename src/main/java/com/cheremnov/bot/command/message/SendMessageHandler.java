package com.cheremnov.bot.command.message;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
@Component
public class SendMessageHandler extends AbstractMessageHandler {

    @Autowired
    private Bot bot;

    @Override
    public boolean handleMessage(Message message, Bot bot) {
        if (!message.hasText()) {
            bot.sendText(message.getChatId(), "Пока можно отправлять только текст...");
            return false;
        }
        bot.sendAllSubscribers(message.getText());
        return true;
    }
}
