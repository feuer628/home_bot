package com.cheremnov.bot.command;

import java.text.MessageFormat;
import java.util.Collections;

public class Unknown extends AbstractCommand {

    public Unknown(String command) {
        super(Collections.singletonList(command));
    }

    @Override
    protected String getMessageText() {
        return MessageFormat.format("����������� ������� \"{0}\". " +
                "\n��� ��������� �������������� �������� ������� ������� /help", args.get(0));
    }
}
