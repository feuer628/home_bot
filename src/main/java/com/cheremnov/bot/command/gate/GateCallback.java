package com.cheremnov.bot.command.gate;

import com.cheremnov.bot.messenger.KeyButton;
import com.cheremnov.bot.messenger.Keyboard;
import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.door.TuyaAdapter;
import com.cheremnov.bot.command.ICallbackHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component
public class GateCallback implements ICallbackHandler {

    private static final String ACTION_OPEN = "open";
    private static final String ACTION_STOP = "stop";
    private static final String ACTION_CLOSE = "close";

    @Override
    public String callbackPrefix() {
        return "gate";
    }

    public Keyboard getGateKeyboard() {
        KeyButton open = KeyButton.callbackButton("Открыть", callbackPrefix() + ":" + ACTION_OPEN);
        KeyButton stop = KeyButton.callbackButton("Стоп", callbackPrefix() + ":" + ACTION_STOP);
        KeyButton close = KeyButton.callbackButton("Закрыть", callbackPrefix() + ":" + ACTION_CLOSE);
        return Keyboard.row(Arrays.asList(open, stop, close));
    }

    public Keyboard getStopOnlyKeyboard() {
        KeyButton stop = KeyButton.callbackButton("Стоп", callbackPrefix() + ":" + ACTION_STOP);
        return Keyboard.row(Collections.singletonList(stop));
    }

    @Override
    public void handle(MsgCallback callback, Messenger messenger) {
        String action = getCallbackInfo(callback);
        try {
            boolean success;
            switch (action) {
                case ACTION_OPEN:
                    success = TuyaAdapter.openGate();
                    break;
                case ACTION_STOP:
                    success = TuyaAdapter.stopGate();
                    break;
                case ACTION_CLOSE:
                    success = TuyaAdapter.closeGate();
                    break;
                default:
                    success = false;
            }
            if (!success) {
                messenger.sendText(callback.getMessage().getChatId(), "Ошибка");
            }
        } catch (Exception e) {
            messenger.sendText(callback.getMessage().getChatId(), "Ошибка: " + e.getMessage());
        }

        messenger.answerCallback(callback);
        if (ACTION_STOP.equals(action)) {
            messenger.deleteInlineMarkup(callback.getMessage());
        } else {
            messenger.setInlineMarkup(callback.getMessage(), getStopOnlyKeyboard());
        }
    }
}