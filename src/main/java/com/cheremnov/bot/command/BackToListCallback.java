package com.cheremnov.bot.command;

import com.cheremnov.bot.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class BackToListCallback implements ICallbackHandler {

    @Autowired
    ApplicationContext context;

    @Override
    public String callbackPrefix() {
        return "back_to_list";
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        String[] params = getCallbacksInfo(callback);
        AbstractPagebleCommand<PageableModel> userListCommand;
        try {
            userListCommand = (AbstractPagebleCommand<PageableModel>) context.getBean(Class.forName(params[0]));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        bot.editMessageText(callback.getMessage().getChatId(), callback.getMessage().getMessageId(), userListCommand.getCommandDescription(), userListCommand.getInlineKeyboard(Integer.parseInt(params[1])));
        bot.answerCallback(callback);
    }
}
