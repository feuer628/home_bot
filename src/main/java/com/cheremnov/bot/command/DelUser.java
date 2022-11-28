package com.cheremnov.bot.command;

import java.util.List;

/**
 * @author cheremnov
 *         Date: 14.11.2022
 */
public class DelUser extends AbstractCommand {
    public static final String COMMAND_NAME = "del_user";

    public DelUser(List<String> args) {
        super(COMMAND_NAME, args);
    }
}
