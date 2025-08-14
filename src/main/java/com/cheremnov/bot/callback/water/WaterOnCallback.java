package com.cheremnov.bot.callback.water;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.callback.ICallbackHandler;
import com.cheremnov.bot.command.AddUserCommand;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class WaterOnCallback  implements ICallbackHandler {

    public InlineKeyboardButton getInlineButton() {
        InlineKeyboardButton yes = new InlineKeyboardButton("Открыть");
        yes.setCallbackData(callbackPrefix());
        return yes;
    }

    @Override
    public String callbackPrefix() {
        return "water_on";
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        bot.editMessageText(callback.getMessage().getChatId(), callback.getMessage().getMessageId(), "Отправлен запрос на включение воды \uD83D\uDEB0");
        bot.answerCallback(callback, null);
    }
}
