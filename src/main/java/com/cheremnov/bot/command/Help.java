package com.cheremnov.bot.command;

import java.util.List;

/**
 * @author cheremnov
 *         Date: 14.11.2022
 */
public class Help extends AbstractCommand {
    public static final String COMMAND_NAME = "/help";

    public Help(List<String> args) {
        super(COMMAND_NAME, args);
    }

    @Override
    protected String getMessageText() {
        return "Для управления автоматикой...\nДля получения сведений...";
    }
}
