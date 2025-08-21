package com.cheremnov.bot.command.water;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.ICallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class WaterOffCallback  implements ICallbackHandler {

    public InlineKeyboardButton getInlineButton() {
        return InlineKeyboardButton.builder().text("Закрыть").callbackData(callbackPrefix()).build();
    }

    @Override
    public String callbackPrefix() {
        return "water_off";
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        bot.sendText(callback.getMessage().getChatId(), " Отправлен запрос на вЫключение воды \uD83D\uDEB0");
        bot.answerCallback(callback);
    }
}
