package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOnOffCommand extends AbstractCommand {

    public AbstractOnOffCommand(List<String> args) {
        super(args);
    }

    @Override
    public void doAction(SendMessage message) {
        if (args.size() == 1) {
            message.setText(getMessageText());
            message.setReplyMarkup(getInlineBottomOnOff());
        } else {
            if ("on".equalsIgnoreCase(args.get(1))) {
                on(message);
            } else {
                off(message);
            }
        }
    }

    abstract void on(SendMessage message);
    abstract void off(SendMessage message);

    private InlineKeyboardMarkup getInlineBottomOnOff() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton on = new InlineKeyboardButton("Вкл");
        on.setText("Включить");
        on.setCallbackData(args.get(0) + " on");
        InlineKeyboardButton off = new InlineKeyboardButton("Выкл");
        off.setText("Выключить");
        off.setCallbackData(args.get(0) + " off");
        row.add(on);
        row.add(off);
        buttons.add(row);
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);
        return markupKeyboard;
    }
}
