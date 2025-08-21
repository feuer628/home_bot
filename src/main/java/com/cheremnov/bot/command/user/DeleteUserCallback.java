package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

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
    public void handle(CallbackQuery callback, Bot bot) {
        trustedUserRepository.deleteById(Long.valueOf(getCallbackInfo(callback)));
        bot.deleteInlineMarkup(callback.getMessage());
        bot.answerCallback(callback);
        bot.sendText(callback.getMessage().getChatId(), "Пользователь удален");
    }
}
