package com.cheremnov.bot.command;

public class Unknown extends AbstractCommand {

    @Override
    protected String getMessageText() {
        return "Неизвестная команда. \nДля получения дополнительных сведений введите команду /help";
    }
}
