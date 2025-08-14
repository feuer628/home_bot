package com.cheremnov.bot.callback;

import com.cheremnov.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface ICallbackHandler {

    String callbackPrefix();

    InlineKeyboardButton getInlineButton();

    void handle(CallbackQuery callback, Bot bot);
}
