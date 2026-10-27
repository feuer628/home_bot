package com.cheremnov.bot.command.user;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.command.AbstractPagebleCommand;
import com.cheremnov.bot.db.trusted_user.TrustedUser;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserListCommand extends AbstractPagebleCommand<TrustedUser> {

    @Autowired
    public UserListCommand(TrustedUserRepository trustedUserRepository) {
        super(trustedUserRepository, TrustedUserCallback.class);
    }

    @Override
    public String getCommandName() {
        return "user_list";
    }

    @Override
    public String getCommandDescription() {
        return "Список доверенных пользователей";
    }

    @Override
    public void handleCommand(MsgMessage message, Messenger messenger) {
        messenger.sendText(message.getChatId(), getCommandDescription(), getInlineKeyboard(0));
    }

}