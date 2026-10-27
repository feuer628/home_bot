package com.cheremnov.bot.command.user;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void handle(MsgCallback callback, Messenger messenger) {
        subscriberRepository.deleteById(getCallbackInfo(callback));
        messenger.deleteInlineMarkup(callback.getMessage().getChatId(), callback.getMessage().getMessageId());
        messenger.sendText(callback.getMessage().getChatId(), "Добавление пользователя отменено");
        messenger.answerCallback(callback);
    }
}