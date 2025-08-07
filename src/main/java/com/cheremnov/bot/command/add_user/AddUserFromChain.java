package com.cheremnov.bot.command.add_user;

import com.cheremnov.bot.command.AbstractChainCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class AddUserFromChain extends AbstractChainCommand {

    private final String trustedUserId;
    private final String trustedUserDescription;

    public AddUserFromChain(Update update, Map<String, String> accumulatorData) {
        super(update, accumulatorData);
        trustedUserDescription = update.getMessage().getText();
        trustedUserId = accumulatorData.get(HandleReplayMessage.ADDED_USR_ID);
    }

    @Override
    public void doAction(SendMessage message) {
        trustedUsers.put(Long.valueOf(trustedUserId), trustedUserDescription);
        super.doAction(message);
    }

    @Override
    protected String getMessageText() {
        return "Пользователь " + trustedUserDescription + " (" + trustedUserId + ") успешно добавлен с список доверенных";
    }
}
