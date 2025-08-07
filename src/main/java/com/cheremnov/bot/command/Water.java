package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class Water extends AbstractOnOffCommand {

    public static final String COMMAND_NAME = "water";

    public Water(List<String> args) {
        super(COMMAND_NAME, args);
    }

    @Override
    protected String getMessageText() {
        return "Что нужно сделать с водой?";
    }

    @Override
    void on(SendMessage message) {
        message.setText("Вода включена");
    }

    @Override
    void off(SendMessage message) {
        message.setText("Вода выключена");
    }
}
