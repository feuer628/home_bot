package com.cheremnov.bot.command.alice;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.stereotype.Component;

@Component
public class SayAliceCallback extends AbstractCallbackHandler {

    @Override
    public String callbackPrefix() {
        return "say";
    }

    @Override
    public void handleCallback(MsgCallback callback, Messenger messenger) {
        messenger.sendText(callback.getMessage().getChatId(), "Отправьте в этот чат сообщение, которое хотите передать Алисе");
        AliceMessageHandler messageHandler = getBean(AliceMessageHandler.class);
        messageHandler.setAliceId(getCallbackInfo(callback));
        messenger.setMessageHandler(callback.getMessage().getChatId(), messageHandler);
        messenger.deleteInlineMarkup(callback.getMessage());
    }
}