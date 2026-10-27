package com.cheremnov.bot.command;

import com.cheremnov.bot.messenger.KeyButton;
import com.cheremnov.bot.messenger.Keyboard;
import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;

import java.util.Arrays;
import java.util.Collections;

public interface ICallbackHandler {

    String callbackPrefix();

    default String getInlineButtonText() {
        return "Нет текста для кнопки";
    }

    default Keyboard getSingleButton(String buttonText, String callbackData) {
        return Keyboard.single(getInlineButton(buttonText, callbackData));
    }

    default Keyboard getSingleButtonWithCancel(String buttonText, String callbackData) {
        KeyButton cancel = KeyButton.callbackButton("Отмена", "cancel");
        return Keyboard.row(Arrays.asList(getInlineButton(buttonText, callbackData), cancel));
    }

    default KeyButton getInlineButton() {
        return KeyButton.callbackButton(getInlineButtonText(), callbackPrefix());
    }

    default KeyButton getInlineButton(String callbackData) {
        KeyButton button = getInlineButton();
        button.setCallbackData(button.getCallbackData() + ":" + callbackData);
        return button;
    }

    default KeyButton getInlineButton(String buttonText, String... callbackData) {
        return KeyButton.callbackButton(buttonText, callbackPrefix() + ":" + String.join(";", callbackData));
    }

    default String getCallbackInfo(MsgCallback callback) {
        String data = callback.getData();
        if (data == null) {
            return null;
        }
        int indexOf = data.indexOf(':');
        return indexOf >= 0 ? data.substring(indexOf + 1) : null;
    }

    default String[] getCallbacksInfo(MsgCallback callback) {
        String info = getCallbackInfo(callback);
        return info == null ? new String[]{} : info.split(";");
    }

    void handle(MsgCallback callback, Messenger messenger);
}