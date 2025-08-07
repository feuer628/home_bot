package com.cheremnov.bot.command;

import java.util.List;

/**
 * @author cheremnov
 *         Date: 14.11.2022
 */
public class Info extends AbstractCommand {
    public static final String COMMAND_NAME = "/info";

    public Info(List<String> args) {
        super(COMMAND_NAME, args);
    }
}
