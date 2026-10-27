package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteGroupCallback extends AbstractCallbackHandler {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public String callbackPrefix() {
        return "delete_group";
    }

    @Override
    public void handleCallback(MsgCallback callback, Messenger messenger) {
        groupRepository.deleteById(Long.valueOf(getCallbackInfo(callback)));
        callback.setData(callbackPrefix() + ":0");
        getBean(GroupListCallback.class).handleCallback(callback, messenger);
    }
}