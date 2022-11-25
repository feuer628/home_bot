package com.cheremnov.bot.command;

import java.util.List;

/**
 * @author cheremnov
 *         Date: 14.11.2022
 */
public class Help extends AbstractCommand {
    public Help(List<String> args) {
        super(args);
    }

    @Override
    protected String getMessageText() {
        return "Для управления автоматикой...\nДля получения сведений...";
    }
}
