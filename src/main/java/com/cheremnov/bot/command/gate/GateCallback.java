package com.cheremnov.bot.command.gate;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.door.TuyaAdapter;
import com.cheremnov.bot.command.ICallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.Collections;

@Component
public class GateCallback implements ICallbackHandler {

    private static final String ACTION_OPEN = "open";
    private static final String ACTION_STOP = "stop";
    private static final String ACTION_CLOSE = "close";

    @Override
    public String callbackPrefix() {
        return "gate";
    }

    public InlineKeyboardMarkup getGateKeyboard() {
        InlineKeyboardButton open = new InlineKeyboardButton("Открыть");
        open.setCallbackData(callbackPrefix() + ":" + ACTION_OPEN);
        InlineKeyboardButton stop = new InlineKeyboardButton("Стоп");
        stop.setCallbackData(callbackPrefix() + ":" + ACTION_STOP);
        InlineKeyboardButton close = new InlineKeyboardButton("Закрыть");
        close.setCallbackData(callbackPrefix() + ":" + ACTION_CLOSE);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(Collections.singletonList(Arrays.asList(open, stop, close)));
        return markup;
    }

    public InlineKeyboardMarkup getStopOnlyKeyboard() {
        InlineKeyboardButton stop = new InlineKeyboardButton("Стоп");
        stop.setCallbackData(callbackPrefix() + ":" + ACTION_STOP);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(Collections.singletonList(Collections.singletonList(stop)));
        return markup;
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        String action = getCallbackInfo(callback);
        try {
            boolean success;
            switch (action) {
                case ACTION_OPEN:
                    success = TuyaAdapter.openGate();
                    break;
                case ACTION_STOP:
                    success = TuyaAdapter.stopGate();
                    break;
                case ACTION_CLOSE:
                    success = TuyaAdapter.closeGate();
                    break;
                default:
                    success = false;
            }
            if (!success) {
                bot.sendText(callback.getMessage().getChatId(), "Ошибка");
            }
        } catch (Exception e) {
            bot.sendText(callback.getMessage().getChatId(), "Ошибка: " + e.getMessage());
        }

        bot.answerCallback(callback);
        if (ACTION_STOP.equals(action)) {
            bot.deleteInlineMarkup(callback.getMessage());
        } else {
            bot.setInlineMarkup(callback.getMessage(), getStopOnlyKeyboard());
        }
    }
}
