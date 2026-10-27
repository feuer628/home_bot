package com.cheremnov.bot.command.user;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteUserCallback implements ICallbackHandler {

    @Autowired
    private TrustedUserRepository trustedUserRepository;

    @Override
    public String callbackPrefix() {
        return "delete_user";
    }

    @Override
    public String getInlineButtonText() {
        return "Удалить";
    }

    @Override
    public void handle(MsgCallback callback, Messenger messenger) {
        trustedUserRepository.deleteById(getCallbackInfo(callback));
        messenger.deleteInlineMarkup(callback.getMessage().getChatId(), callback.getMessage().getMessageId());
        messenger.answerCallback(callback);
        messenger.sendText(callback.getMessage().getChatId(), "Пользователь удален");
    }
}