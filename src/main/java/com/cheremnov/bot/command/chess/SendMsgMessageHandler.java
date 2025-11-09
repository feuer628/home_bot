package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractMessageHandler;
import com.cheremnov.bot.messages.MessageSender;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class SendMsgMessageHandler extends AbstractMessageHandler {

    @Setter
    private List<Long> subscriberChatIds;

    @Setter
    private String groupName;

    @Autowired
    private MessageSender messageSender;

    @Override
    public boolean handleMessage(Message message, Bot bot) {
        String messageText = "Группа " + groupName + ":\n\n" + message.getText();
        if (subscriberChatIds != null) {
            subscriberChatIds.forEach(chatId -> bot.sendText(chatId, messageText));
        }
        return true;
    }
}
