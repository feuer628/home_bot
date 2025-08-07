package com.cheremnov.bot.command.add_user;

import com.cheremnov.bot.command.AbstractChainCommand;
import com.cheremnov.bot.command.AbstractCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class SetUserDescriptionCommand extends AbstractChainCommand {



    public SetUserDescriptionCommand(Update update, Map<String, String> accumulatorData) {
        super(update, accumulatorData);
    }

    @Override
    public void doAction(SendMessage message) {
        super.doAction(message);
    }

    @Override
    protected String getMessageText() {
        return "Введите псевдоним для добавляемого пользователя";
    }

    @Override
    public Class<? extends AbstractCommand> nextCommand() {
        return AddUserFromChain.class;
    }
}
