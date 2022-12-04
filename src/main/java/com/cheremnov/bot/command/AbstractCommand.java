package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.*;

public abstract class AbstractCommand {

    protected final Map<Long, String> trustedUsers = new HashMap<>(Collections.singletonMap(SUPER_ADMIN_ID, "Админ"));

    private static final long SUPER_ADMIN_ID = 410563696;
    protected final List<String> args;
    protected String commandName;

    public String getCommandName() {
        return commandName;
    }

    public AbstractCommand() {
        this(null);
    }

    public AbstractCommand(String commandName) {
        this(commandName, Collections.emptyList());
    }

    public AbstractCommand(String commandName, List<String> args) {
        this.commandName = commandName;
        this.args = args;
    }

    public boolean checkRight(Long id) {
        return trustedUsers.containsKey(id);
    }

    public void parseAndCheckArgs() {

    }

    public void doAction(SendMessage message) {
        message.setText(getMessageText());
        additionalAction();
    }

    protected void additionalAction() {

    }

    protected String getMessageText() {
        return "?????????????";
    }

    public Class<? extends AbstractCommand> nextCommand() {
        return null;
    }
}
