package com.cheremnov.bot.command;

import com.cheremnov.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.Collections;

public interface ICallbackHandler {

    String callbackPrefix();

    default String getInlineButtonText() {
        return "Нет текста для кнопки";
    }

    default InlineKeyboardMarkup getSingleButton(String buttonText, String callbackData) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(Collections.singletonList(getInlineButton(buttonText, callbackData))));
        return inlineKeyboardMarkup;
    }

    default InlineKeyboardMarkup getSingleButtonWithCancel(String buttonText, String callbackData) {
        InlineKeyboardButton cancel =  new InlineKeyboardButton("Отмена");
        cancel.setCallbackData("cancel");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(Arrays.asList(getInlineButton(buttonText, callbackData), cancel)));
        return inlineKeyboardMarkup;
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

    default InlineKeyboardButton getInlineButton(String buttonText, String... callbackData) {
        InlineKeyboardButton button =  new InlineKeyboardButton(buttonText);
        button.setCallbackData(callbackPrefix() + ":" + String.join(";", callbackData));
        return button;
    }

    default String getCallbackInfo(CallbackQuery callback) {
        String data = callback.getData();
        int indexOf = data.indexOf(':');
        return indexOf >= 0 ? data.substring(indexOf + 1) : null;
    }

    default String[] getCallbacksInfo(CallbackQuery callback) {
        String info = getCallbackInfo(callback);
        return info == null ? new String[]{} : info.split(";");
    }

    void handle(CallbackQuery callback, Bot bot);
}
