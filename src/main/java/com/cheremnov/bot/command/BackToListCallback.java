package com.cheremnov.bot.command;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BackToListCallback implements ICallbackHandler {

    @Autowired
    ApplicationContext context;

    @Override
    public String callbackPrefix() {
        return "back_to_list";
    }

    @Override
    public void handle(MsgCallback callback, Messenger messenger) {
        String[] params = getCallbacksInfo(callback);
        AbstractPagebleCommand<PageableModel> userListCommand;
        try {
            userListCommand = (AbstractPagebleCommand<PageableModel>) context.getBean(Class.forName(params[0]));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        messenger.editMessageText(callback.getMessage().getChatId(), callback.getMessage().getMessageId(),
                userListCommand.getCommandDescription(), userListCommand.getInlineKeyboard(Integer.parseInt(params[1])));
        messenger.answerCallback(callback);
    }
}