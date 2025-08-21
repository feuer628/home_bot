package com.cheremnov.bot.command;

import com.cheremnov.bot.Bot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CancelCallback implements ICallbackHandler {

    @Override
    public String callbackPrefix() {
        return "cancel";
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        bot.deleteInlineMarkup(callback.getMessage());
        bot.sendText(callback.getMessage().getChatId(), "Действие отменено");
    }
}
