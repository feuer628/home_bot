package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendMessageToGroupCallback extends AbstractCallbackHandler {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public String callbackPrefix() {
        return "send_msg_toGroup";
    }

    @Override
    public void handleCallback(MsgCallback callback, Messenger messenger) {
        GroupTour groupTour = groupRepository.findById(Long.valueOf(getCallbackInfo(callback))).orElseThrow();
        messenger.sendText(callback.getMessage().getChatId(), "Отправьте в этот чат сообщение, которое хотите отправить в группу " + groupTour.getName());
        SendMsgMessageHandler messageHandler = getBean(SendMsgMessageHandler.class);
        messageHandler.setSubscriberChatIds(groupTour.getSubscriberChatIds());
        messenger.setMessageHandler(callback.getMessage().getChatId(), messageHandler);
        messenger.deleteInlineMarkup(callback.getMessage());
    }
}