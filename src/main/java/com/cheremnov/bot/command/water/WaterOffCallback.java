package com.cheremnov.bot.command.water;

import com.cheremnov.bot.messenger.KeyButton;
import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.ICallbackHandler;
import org.springframework.stereotype.Component;

@Component
public class WaterOffCallback implements ICallbackHandler {

    public KeyButton getInlineButton() {
        return KeyButton.callbackButton("Закрыть", callbackPrefix());
    }

    @Override
    public String callbackPrefix() {
        return "water_off";
    }

    @Override
    public void handle(MsgCallback callback, Messenger messenger) {
        messenger.sendText(callback.getMessage().getChatId(), " Отправлен запрос на вЫключение воды \uD83D\uDEB0");
        messenger.answerCallback(callback);
    }
}