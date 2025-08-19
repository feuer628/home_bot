package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.ICallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class UserListCallback implements ICallbackHandler {

    @Autowired
    ApplicationContext context;

    @Override
    public String callbackPrefix() {
        return "user_list";
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        UserListCommand userListCommand = context.getBean(UserListCommand.class);
        bot.editMessageText(callback.getMessage().getChatId(), callback.getMessage().getMessageId(), userListCommand.getCommandDescription(), userListCommand.getInlineKeyboard(Integer.parseInt(getCallbackInfo(callback))));
        bot.answerCallback(callback, null);
    }
}
