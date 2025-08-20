package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class CancelAddUserCallBack implements ICallbackHandler {

    @Autowired
    public SubscriberRepository subscriberRepository;

    @Override
    public String callbackPrefix() {
        return "cancelAddUser";
    }

    public String getInlineButtonText() {
        return "Отмена";
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        subscriberRepository.deleteById(Long.valueOf(getCallbackInfo(callback)));
        bot.deleteInlineMarkup(callback.getMessage());
        bot.restoreDefaultMessageHandler();
        bot.sendText(callback.getMessage().getChatId(), "Добавление пользователя отменено");
        bot.answerCallback(callback, null);
    }
}
