package com.cheremnov.bot.command;

import com.cheremnov.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface ICallbackHandler {

    String callbackPrefix();

    default String getInlineButtonText() {
        return "Нет текста для кнопки";
    }

    default InlineKeyboardButton getInlineButton() {
        InlineKeyboardButton button = new InlineKeyboardButton(getInlineButtonText());
        button.setCallbackData(callbackPrefix());
        return button;
    }

    default InlineKeyboardButton getInlineButton(String callbackData) {
        InlineKeyboardButton button = getInlineButton();
        button.setCallbackData(button.getCallbackData() + ":" + callbackData);
        return button;
    }

    default InlineKeyboardButton getInlineButton(String buttonText, String callbackData) {
        InlineKeyboardButton button =  new InlineKeyboardButton(buttonText);
        button.setCallbackData(callbackPrefix() + ":" + callbackData);
        return button;
    }

    default String getCallbackInfo(CallbackQuery callback) {
        String data = callback.getData();
        int indexOf = data.indexOf(':');
        return indexOf >= 0 ? data.substring(indexOf + 1) : null;
    }

    void handle(CallbackQuery callback, Bot bot);
}
