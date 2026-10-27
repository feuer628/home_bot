package com.cheremnov.bot.command.door;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.ICallbackHandler;
import org.springframework.stereotype.Component;

@Component
public class DoorCallback implements ICallbackHandler {

    @Override
    public String callbackPrefix() {
        return "door";
    }

    @Override
    public void handle(MsgCallback callback, Messenger messenger) {
        try {
            if (!TuyaAdapter.openDoor()) {
                messenger.sendText(callback.getMessage().getChatId(), "Ошибка");
            }
        } catch (Exception e) {
            messenger.sendText(callback.getMessage().getChatId(), "Ошибка: " + e.getMessage());
        }

        messenger.answerCallback(callback);
        messenger.deleteInlineMarkup(callback.getMessage().getChatId(), callback.getMessage().getMessageId());
    }
}