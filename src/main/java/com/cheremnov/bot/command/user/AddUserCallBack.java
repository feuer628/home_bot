package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.db.subscibers.Subscriber;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import com.cheremnov.bot.db.trusted_user.TrustedUser;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import com.cheremnov.bot.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
@Slf4j
public class AddUserCallBack implements ICallbackHandler {

    @Autowired
    TrustedUserRepository trustedUserRepository;

    @Autowired
    public SubscriberRepository subscriberRepository;

    @Override
    public String callbackPrefix() {
        return "addUser";
    }

    @Override
    public InlineKeyboardButton getInlineButton() {
        return InlineKeyboardButton.builder().text("Добавить").callbackData(callbackPrefix()).build();
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        bot.deleteInlineMarkup(callback.getMessage());
        Subscriber subscriber = subscriberRepository.findById(Long.valueOf(getCallbackInfo(callback))).orElseThrow();

        TrustedUser trustedUser = new TrustedUser();
        trustedUser.setId(subscriber.getId());
        trustedUser.setName(subscriber.getName());

        trustedUserRepository.save(trustedUser);
        bot.restoreDefaultMessageHandler();
        bot.sendText(callback.getMessage().getChatId(), "Пользователь добавлен в список доверенных");
        bot.answerCallback(callback, null);
    }
}
