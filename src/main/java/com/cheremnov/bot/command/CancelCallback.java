package com.cheremnov.bot.command;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import org.springframework.stereotype.Component;

@Component
public class CancelCallback implements ICallbackHandler {

    @Override
    public String callbackPrefix() {
        return "cancel";
    }

    @Override
    public void handle(MsgCallback callback, Messenger messenger) {
        messenger.deleteInlineMarkup(callback.getMessage().getChatId(), callback.getMessage().getMessageId());
        messenger.sendText(callback.getMessage().getChatId(), "Действие отменено");
    }
}