package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Commands {
    WATER("/water", "Управление водой", Water.class),
    ELECTRIC("/electric", "Управление электричеством", Electric.class),
    GAS("/gas", "Управление газом", Gas.class),
    REGISTER_USER("/list_user", "Список пользователей", UsersList.class),
    ADD_USER("/add_user", "Добавление пользователя", AddUser.class),
    DEL_USER("/del_user", "Удаление пользователя", DelUser.class),
    INFO("/info", "Информация о боте", Info.class),
    HELP("/help", "Основные команды бота", Help.class);
    //UNKNOWN("/unknown", "", Unknown.class, true);

    private final String commandName;
    private final String description;
    private final Class<? extends AbstractCommand> commandClass;

    private final boolean hidden;

    Commands(String commandName, String description, Class<? extends AbstractCommand> commandClass) {
        this(commandName, description, commandClass, false);
    }

    Commands(String commandName, String description, Class<? extends AbstractCommand> commandClass, boolean hidden) {
        this.commandName = commandName;
        this.description = description;
        this.commandClass = commandClass;
        this.hidden = hidden;
    }

    public static AbstractCommand getCommandForMessage(String command) {
        System.out.println("command = " + command);
        for (Commands commands : values()) {
            if (command.startsWith(commands.commandName)) {
                try {
                    List<String> args =
                            Arrays.stream(command.trim().split(" ")).
                                    filter(s -> !s.isEmpty()).collect(Collectors.toList());

                    return commands.commandClass.getDeclaredConstructor(List.class).newInstance(args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("Unknown command " + command);
        return new Unknown(command);
    }

    public static List<BotCommand> getMenuCommands() {
        List<BotCommand> menuCommandList = new ArrayList<>();
        for (Commands command : values()) {
            if (!command.hidden) {
                menuCommandList.add(new BotCommand(command.commandName, command.description));
            }
        }
        System.out.println(menuCommandList.size());
        return menuCommandList;
    }
}
