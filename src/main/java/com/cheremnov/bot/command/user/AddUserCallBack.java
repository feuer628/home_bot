package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.security.UserChecker;
import com.cheremnov.bot.db.subscibers.Subscriber;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import com.cheremnov.bot.db.trusted_user.TrustedUser;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
@Slf4j
public class AddUserCallBack implements ICallbackHandler {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private TrustedUserRepository trustedUserRepository;

    @Autowired
    private UserChecker userChecker;

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
        // после сохранения нового доверенного пользователя
        // перезагружаем список доверенных пользователей
        // чтобы новый сразу оказался в кешированном списке
        userChecker.reset();


        bot.sendText(callback.getMessage().getChatId(), "Пользователь добавлен в список доверенных");
        bot.answerCallback(callback);
    }
}
