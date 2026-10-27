package com.cheremnov.bot.command.water;

import com.cheremnov.bot.messenger.KeyButton;
import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.ICallbackHandler;
import org.springframework.stereotype.Component;

@Component
public class WaterOnCallback implements ICallbackHandler {

    public KeyButton getInlineButton() {
        return KeyButton.callbackButton("Открыть", callbackPrefix());
    }

    @Override
    public String callbackPrefix() {
        return "water_on";
    }

    @Override
    public void handle(MsgCallback callback, Messenger messenger) {
        messenger.editMessageText(callback.getMessage().getChatId(), callback.getMessage().getMessageId(),
                "Отправлен запрос на включение воды \uD83D\uDEB0", null);
        messenger.answerCallback(callback);
    }
}