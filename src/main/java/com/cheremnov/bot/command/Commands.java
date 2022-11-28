package com.cheremnov.bot.command;

import com.cheremnov.bot.command.add_user.AddUser;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Commands {
    WATER(Water.COMMAND_NAME, "���������� �����", Water.class),
    ELECTRIC(Electric.COMMAND_NAME, "���������� ��������������", Electric.class),
    GAS(Gas.COMMAND_NAME, "���������� �����", Gas.class),
    REGISTER_USER(UsersList.COMMAND_NAME, "������ �������������", UsersList.class),
    ADD_USER(AddUser.COMMAND_NAME, "���������� ������������", AddUser.class),
    DEL_USER(DelUser.COMMAND_NAME, "�������� ������������", DelUser.class),
    INFO(Info.COMMAND_NAME, "���������� � ����", Info.class),
    HELP(Help.COMMAND_NAME, "�������� ������� ����", Help.class);

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
                            Arrays.stream(command.replace(commands.commandName, "").trim().split(" ")).
                                    filter(s -> !s.isEmpty()).collect(Collectors.toList());

                    return commands.commandClass.getDeclaredConstructor(List.class).newInstance(args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
