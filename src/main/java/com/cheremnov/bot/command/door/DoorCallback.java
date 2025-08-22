package com.cheremnov.bot.command.door;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.ICallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class DoorCallback implements ICallbackHandler {

    @Override
    public String callbackPrefix() {
        return "door";
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        try {
            if (!TuyaAdapter.openDoor()) {
                bot.sendText(callback.getMessage().getChatId(), "Ошибка");
            }
        } catch (Exception e) {
            bot.sendText(callback.getMessage().getChatId(), "Ошибка: " + e.getMessage());
        }

        bot.answerCallback(callback);
        bot.deleteInlineMarkup(callback.getMessage());
    }
}
