package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class Electric extends AbstractOnOffCommand {

    public Electric(List<String> args) {
        super(args);
    }

    @Override
    protected String getMessageText() {
        return "Что нужно сделать с электричеством?";
    }

    @Override
    void on(SendMessage message) {
        message.setText("Электричество включено");
    }

    @Override
    void off(SendMessage message) {
        message.setText("Электричество выключено");
    }
}
