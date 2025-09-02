package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class SendMessageToGroupCallback extends AbstractCallbackHandler {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public String callbackPrefix() {
        return "send_msg_toGroup";
    }

    @Override
    public void handleCallback(CallbackQuery callback, Bot bot) {
        GroupTour groupTour = groupRepository.findById(Long.valueOf(getCallbackInfo(callback))).orElseThrow();
        bot.sendText(callback.getMessage().getChatId(), "Отправьте в этот чат сообщение, которое хотите отправить в группу " + groupTour.getName());
        SendMsgMessageHandler messageHandler = getBean(SendMsgMessageHandler.class);
        messageHandler.setSubscriberChatIds(groupTour.getSubscriberChatIds());
        bot.setMessageHandler(callback.getMessage().getChatId(), messageHandler);
        bot.deleteInlineMarkup(callback.getMessage());
    }
}
