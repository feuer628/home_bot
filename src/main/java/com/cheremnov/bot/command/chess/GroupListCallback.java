package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.stereotype.Component;

@Component
public class GroupListCallback extends AbstractCallbackHandler {
    @Override
    public String callbackPrefix() {
        return "group_list";
    }

    @Override
    public void handleCallback(MsgCallback callback, Messenger messenger) {
        GroupListCommand groupListCommand = getBean(GroupListCommand.class);
        messenger.editMessageText(callback.getMessage().getChatId(), callback.getMessage().getMessageId(),
                groupListCommand.getCommandDescription(), groupListCommand.getInlineKeyboard(Integer.parseInt(getCallbackInfo(callback))));
        messenger.answerCallback(callback);
    }
}