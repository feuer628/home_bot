package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractPagebleCommand;
import com.cheremnov.bot.db.trusted_user.TrustedUser;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class UserListCommand extends AbstractPagebleCommand<TrustedUser> {

    public boolean isCommandHidden() {
        return true;
    }

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
    public void handleCommand(Message message, Bot bot) {
        bot.sendText(message.getChatId(), getCommandDescription(), getInlineKeyboard(0));
    }

}
