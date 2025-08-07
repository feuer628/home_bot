package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class YesNoCommand extends AbstractCommand {

    private final String confirmMessage;

    public YesNoCommand(String confirmMessage) {
        this.confirmMessage = confirmMessage;
    }

    @Override
    public void doAction(SendMessage message) {
        super.doAction(message);
        message.setReplyMarkup(getInlineBottomOnOff());
    }

    @Override
    protected String getMessageText() {
        return confirmMessage;
    }

    void yes(SendMessage message) {

    }
    void no(SendMessage message) {

    }

    private InlineKeyboardMarkup getInlineBottomOnOff() {
        InlineKeyboardButton yes = new InlineKeyboardButton("Да");
        yes.setCallbackData("yes");
        InlineKeyboardButton no = new InlineKeyboardButton("Нет");
        no.setCallbackData("no");
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(Collections.singletonList(Arrays.asList(yes, no)));
        return markupKeyboard;
    }
}
