package com.cheremnov.bot.command.mangal;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class MangalOrderCallbackHandler extends AbstractCallbackHandler {
    @Override
    public String callbackPrefix() {
        return "order";
    }

    @Override
    public void handleCallback(CallbackQuery callback, Bot bot) {
        bot.sendAllTrustedUsers("Поступила заявка на мангал - " + getCallbackInfo(callback));
    }
}
