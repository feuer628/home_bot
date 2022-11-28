package com.cheremnov.bot.command.add_user;

import com.cheremnov.bot.command.AbstractChainCommand;
import com.cheremnov.bot.command.AbstractCommand;
import com.cheremnov.bot.command.YesNoCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.text.MessageFormat;
import java.util.Map;

public class HandleReplayMessage extends AbstractChainCommand {

    public static final String ADDED_USR_ID = "ADDED_USR_ID";
    private boolean userAdd;

    public HandleReplayMessage(Update update, Map<String, String> accumulatorData) {
        super(update, accumulatorData);
    }

    @Override
    public void doAction(SendMessage message) {
        User forwardUser = update.getMessage().getForwardFrom();
        if (forwardUser != null) {
            Long userId = forwardUser.getId();
            String user = trustedUsers.get(userId);
            if (user == null) {
                String pattern = """
                        Вы дествительно хотете добавить пользователя
                        ФИО: {0}
                        {1}в список доверенных пользователей?""";
                String fio = forwardUser.getFirstName() + " "+  forwardUser.getLastName();
                String userName = forwardUser.getUserName() == null ?
                        "" : "UserName: @" + forwardUser.getUserName() + "\n";
                new YesNoCommand(MessageFormat.format(pattern, fio, userName)).doAction(message);
                accumulatorData.put(ADDED_USR_ID, String.valueOf(userId));
                userAdd = true;
            } else {
                message.setText("Пользователь " + user + " (" + userId + ") уже был добавлен в список доверенных");
            }
        }
    }

    @Override
    public Class<? extends AbstractCommand> nextCommand() {
        return userAdd ? AnswerYesNoQuestions.class : null;
    }
}
