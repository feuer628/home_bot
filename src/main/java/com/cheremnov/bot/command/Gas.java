package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class Gas extends AbstractOnOffCommand {

    public static final String COMMAND_NAME = "gas";

    public Gas(List<String> args) {
        super(COMMAND_NAME, args);
    }

    @Override
    protected String getMessageText() {
        return "Что нужно сделать с газом?";
    }

    @Override
    void on(SendMessage message) {
        message.setText("Газ включен");
    }

    @Override
    void off(SendMessage message) {
        message.setText("Газ выключен");
    }
}
