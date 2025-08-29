package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class DeleteGroupCallback extends AbstractCallbackHandler {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public String callbackPrefix() {
        return "delete_group";
    }

    @Override
    public void handleCallback(CallbackQuery callback, Bot bot) {
        bot.deleteInlineMarkup(callback.getMessage());
        groupRepository.deleteById(Long.valueOf(getCallbackInfo(callback)));
        bot.sendText(callback.getMessage().getChatId(), "Группа удалена");
    }
}
