package com.cheremnov.bot.command.user;

import com.cheremnov.bot.messenger.KeyButton;
import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.security.UserChecker;
import com.cheremnov.bot.db.subscibers.Subscriber;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import com.cheremnov.bot.db.trusted_user.TrustedUser;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public KeyButton getInlineButton() {
        return KeyButton.callbackButton("Добавить", callbackPrefix());
    }

    @Override
    public void handle(MsgCallback callback, Messenger messenger) {
        messenger.deleteInlineMarkup(callback.getMessage().getChatId(), callback.getMessage().getMessageId());
        Subscriber subscriber = subscriberRepository.findById(getCallbackInfo(callback)).orElseThrow();

        TrustedUser trustedUser = new TrustedUser();
        trustedUser.setId(subscriber.getId());
        trustedUser.setName(subscriber.getName());

        trustedUserRepository.save(trustedUser);
        // после сохранения нового доверенного пользователя
        // перезагружаем список доверенных пользователей
        // чтобы новый сразу оказался в кешированном списке
        userChecker.reset();


        messenger.sendText(callback.getMessage().getChatId(), "Пользователь добавлен в список доверенных");
        messenger.answerCallback(callback);
    }
}