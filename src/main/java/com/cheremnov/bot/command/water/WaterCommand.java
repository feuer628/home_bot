package com.cheremnov.bot.command.water;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Arrays;
import java.util.Collections;

@Component
public class WaterCommand extends AbstractCommandHandler {


    @Override
    public String getCommandName() {
        return "water";
    }

    @Override
    public String getCommandDescription() {
        return "Управление водой";
    }

    private InlineKeyboardMarkup getInlineBottomOnOff() {
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(Collections.singletonList(
                Arrays.asList(getBean(WaterOffCallback.class).getInlineButton(), getBean(WaterOnCallback.class).getInlineButton())));
        return markupKeyboard;
    }

    @Override
    public void handleCommand(Message message1, Bot bot) {
        bot.sendText(message1.getChatId(), "Что нужно сделать с водой?", getInlineBottomOnOff());
    }
}
