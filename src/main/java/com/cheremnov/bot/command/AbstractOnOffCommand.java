package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractOnOffCommand extends AbstractCommand {

    public AbstractOnOffCommand(String commandName, List<String> args) {
        super(commandName, args);
    }

    @Override
    public void doAction(SendMessage message) {
        if (args.isEmpty()) {
            message.setText(getMessageText());
            message.setReplyMarkup(getInlineBottomOnOff());
        } else {
            if ("on".equalsIgnoreCase(args.get(0))) {
                on(message);
            } else {
                off(message);
            }
        }
    }

    abstract void on(SendMessage message);
    abstract void off(SendMessage message);

    private InlineKeyboardMarkup getInlineBottomOnOff() {
        InlineKeyboardButton on = new InlineKeyboardButton("Включить");
        on.setCallbackData(commandName + " on");
        InlineKeyboardButton off = new InlineKeyboardButton("Выключить");
        off.setCallbackData(commandName + " off");
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(Collections.singletonList(Arrays.asList(on, off)));
        return markupKeyboard;
    }
}
