package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractOnOffCommand extends AbstractCommand {

    private boolean isFullCommand;

    private boolean isOn;

    public AbstractOnOffCommand(String commandName, List<String> args) {
        super(commandName, args);
    }

    @Override
    public void parseAndCheckArgs() {
        if (args.isEmpty()) {
            return;
        }
        if (args.size() != 1) {
            throw new IllegalArgumentException("Допустимо передавать только один аргумент");
        }
        String arg = args.get(0);
        if (!"on".equalsIgnoreCase(arg) && !"off".equalsIgnoreCase(arg)) {
            throw new IllegalArgumentException("Поддерживаемые аргументы комманды " + commandName + ": on/off");
        }
        isOn = "on".equalsIgnoreCase(arg);
        isFullCommand = true;
    }

    @Override
    public void doAction(SendMessage message) {
        if (!isFullCommand) {
            message.setText(getMessageText());
            message.setReplyMarkup(getInlineBottomOnOff());
        } else {
            if (isOn) {
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
        return isFullCommand ? null : OnOffCommandHandler.class;
    }
}
