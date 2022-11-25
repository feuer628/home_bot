package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class Water extends AbstractOnOffCommand {

    public Water(List<String> args) {
        super(args);
    }

    @Override
    protected String getMessageText() {
        return "��� ����� ������� � �����?";
    }

    @Override
    void on(SendMessage message) {
        message.setText("���� ��������");
    }

    @Override
    void off(SendMessage message) {
        message.setText("���� ���������");
    }
}
