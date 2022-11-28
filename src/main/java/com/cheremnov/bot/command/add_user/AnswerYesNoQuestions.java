package com.cheremnov.bot.command.add_user;

import com.cheremnov.bot.command.AbstractChainCommand;
import com.cheremnov.bot.command.AbstractCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class AnswerYesNoQuestions extends AbstractChainCommand {

    private boolean isYes;

    public AnswerYesNoQuestions(Update update, Map<String, String> accumulatorData) {
        super(update, accumulatorData);
    }

    @Override
    public void doAction(SendMessage message) {
        if ("yes".equalsIgnoreCase(update.getCallbackQuery().getData())) {
            isYes = true;
        }
        super.doAction(message);
    }

    @Override
    protected String getMessageText() {
        return isYes ? "Введите псевдоним для добавляемого пользователя": "Команда отменена";
    }

    @Override
    public Class<? extends AbstractCommand> nextCommand() {
        return isYes ? AddUserFromChain.class : null;
    }
}
