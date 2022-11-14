package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.List;

public enum Commands {
    REGISTER_USER("/list_user", "Список пользователей", new UsersList()),
    ADD_USER("/add_user", "Добавление пользователя", new AddUser()),
    DEL_USER("/del_user", "Удаление пользователя", new DelUser()),
    INFO("/info", "Информация о боте", new Info()),
    HELP("/help", "Основные команды бота", new Help()),
    UNKNOWN("/unknown", "", new Unknown(), true);

    private final String commandName;
    private final String description;
    private final AbstractCommand commandClass;

    private final boolean hidden;

    Commands(String commandName, String description, AbstractCommand commandClass) {
        this(commandName, description, commandClass, false);
    }

    Commands(String commandName, String description, AbstractCommand commandClass, boolean hidden) {
        this.commandName = commandName;
        this.description = description;
        this.commandClass = commandClass;
        this.hidden = hidden;
    }

    public static AbstractCommand getCommandForMessage(String command) {
        //command = command.substring(1);
        System.out.println("command = " + command);
        for (Commands commands : values()) {
            if (command.startsWith(commands.commandName)) {
                return commands.commandClass;
            }
        }
        System.out.println("Unknown command " + command);
        return UNKNOWN.commandClass;
    }

    public static List<BotCommand> getMenuCommands() {
        List<BotCommand> menuCommandList = new ArrayList<>();
        for (Commands command : values()) {
            if (!command.hidden) {
                menuCommandList.add(new BotCommand(command.commandName, command.description));
            }
        }
        return menuCommandList;
    }
}
