package com.cheremnov.bot.command;

import com.cheremnov.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface ICallbackHandler {

    String callbackPrefix();

    InlineKeyboardButton getInlineButton();

    default InlineKeyboardButton getInlineButton(String s) {
        InlineKeyboardButton button = getInlineButton();
        button.setCallbackData(button.getCallbackData() + ":" + s);
        return button;
    }

    default String getCallbackInfo(CallbackQuery callback) {
        String data = callback.getData();
        int indexOf = data.indexOf(':');
        return indexOf >= 0 ? data.substring(indexOf + 1) : null;
    }

    void handle(CallbackQuery callback, Bot bot);
}
