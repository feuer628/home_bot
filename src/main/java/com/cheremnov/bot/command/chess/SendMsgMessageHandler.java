package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.command.AbstractMessageHandler;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendMsgMessageHandler extends AbstractMessageHandler {

    @Setter
    private List<String> subscriberChatIds;

    @Override
    public boolean handleMessage(MsgMessage message, Messenger messenger) {
        if (subscriberChatIds != null) {
            subscriberChatIds.forEach(chatId -> messenger.sendText(chatId, message.getText()));
        }
        return true;
    }
}