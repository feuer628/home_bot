package com.cheremnov.bot.command.mangal;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class MangalOrderCallbackHandler extends AbstractCallbackHandler {
    @Override
    public String callbackPrefix() {
        return "order";
    }

    @Override
    public void handleCallback(CallbackQuery callback, Bot bot) {
        bot.deleteInlineMarkup(callback.getMessage());
        bot.sendText(callback.getMessage().getChatId(), "Заявка отправлена, в ближайшее время с Вами свяжутся для подтверждения заказа.");
        User from = callback.getFrom();
        bot.sendAllTrustedUsers("Поступила заявка на мангал - " + getCallbackInfo(callback) +
                "\nПользователь: @" + from.getUserName() +
                "\nФИО: " + String.join(", ", from.getFirstName(), from.getLastName()));
    }
}
