package com.cheremnov.bot.command;

import java.text.MessageFormat;
import java.util.Collections;

public class Unknown extends AbstractCommand {

    public Unknown(String commandName) {
        super(commandName);
    }

    @Override
    protected String getMessageText() {
        return MessageFormat.format("Неизвестная команда \"{0}\". " +
                "\nДля получения дополнительных сведений введите команду /help", commandName);
    }
}
