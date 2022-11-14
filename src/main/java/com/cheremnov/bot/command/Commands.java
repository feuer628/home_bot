package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.List;

public enum Commands {
    REGISTER_USER("/list_user", "1", new RegisterUser()),
    UNKNOWN("/add_user", "2", new Unknown()),
    UNKNOWN1("/del_user", "3", new Unknown()),
    UNKNOWN3("/info", "4", new Unknown());

    private final String commandName;
    private final String description;
    private final AbstractCommand commandClass;

    Commands(String commandName, String description, AbstractCommand commandClass) {
        this.commandName = commandName;
        this.description = description;
        this.commandClass = commandClass;
    }

    public static AbstractCommand getCommandForMessage(String command) {
        command = "/" + command;
        System.out.println("command = " + command);
        for (Commands commands : values()) {
            if (command.startsWith(commands.commandName)) {
                return commands.commandClass;
            }
        }
        return UNKNOWN.commandClass;
    }

    public static List<BotCommand> getMenuCommands() {
        List<BotCommand> menuCommandList = new ArrayList<>();
        for (Commands command : values()) {
            menuCommandList.add(new BotCommand(command.commandName, command.description));
        }
        return menuCommandList;
    }
}
