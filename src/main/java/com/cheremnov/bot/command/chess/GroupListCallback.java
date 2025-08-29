package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class GroupListCallback extends AbstractCallbackHandler {
    @Override
    public String callbackPrefix() {
        return "group_list";
    }

    @Override
    public void handleCallback(CallbackQuery callback, Bot bot) {
        GroupListCommand groupListCommand = getBean(GroupListCommand.class);
        bot.editMessageText(callback.getMessage().getChatId(), callback.getMessage().getMessageId(), groupListCommand.getCommandDescription(), groupListCommand.getInlineKeyboard(Integer.parseInt(getCallbackInfo(callback))));
        bot.answerCallback(callback);
    }
}
