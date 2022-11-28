package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

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

    private ReplyKeyboardMarkup getInlineBottomOnOff() {
        KeyboardButton on = new KeyboardButton("Включить");
        KeyboardButton off = new KeyboardButton("Выключить");
        ReplyKeyboardMarkup markupKeyboard = new ReplyKeyboardMarkup();
        markupKeyboard.setOneTimeKeyboard(true);
        markupKeyboard.setKeyboard(Collections.singletonList(new KeyboardRow(Arrays.asList(on, off))));
        return markupKeyboard;
    }

    @Override
    public Class<? extends AbstractCommand> nextCommand() {
        return OnOffCommandHandler.class;
    }
}
