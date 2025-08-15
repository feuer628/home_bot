package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.ICallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class CancelAddUserCallBack implements ICallbackHandler {

    @Override
    public String callbackPrefix() {
        return "cancelAddUser";
    }

    @Override
    public InlineKeyboardButton getInlineButton() {
        return InlineKeyboardButton.builder().text("Отмена").callbackData(callbackPrefix()).build();
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        bot.deleteInlineMarkup(callback.getMessage());
        bot.restoreDefaultMessageHandler();
        bot.sendText(callback.getMessage().getChatId(), "Добавление пользователя отменено");
        bot.answerCallback(callback, null);
    }
}
