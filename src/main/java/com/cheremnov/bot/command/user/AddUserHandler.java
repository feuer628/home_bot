package com.cheremnov.bot.command.user;

import com.cheremnov.bot.messenger.Keyboard;
import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.messenger.MsgUser;
import com.cheremnov.bot.command.AbstractMessageHandler;
import com.cheremnov.bot.db.subscibers.Subscriber;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;

@Component
public class AddUserHandler extends AbstractMessageHandler {

    @Autowired
    public TrustedUserRepository trustedUserRepository;

    @Autowired
    public SubscriberRepository subscriberRepository;

    @Override
    public boolean handleMessage(MsgMessage message, Messenger messenger) {
        // тут должно быть пересланное сообщение
        MsgUser forwardUser = message.getForwardedFrom();
        if (forwardUser == null) {
            messenger.sendText(message.getChatId(), "Вы должны переслать сообщение от пользователя, которого хотите добавить в список доверенных");
            return false;
        }
        if (forwardUser.isBot()) {
            messenger.sendText(message.getChatId(), "Ботов нельзя добавлять в список доверенных");
            return false;
        }
        Subscriber trustedUser = new Subscriber();
        trustedUser.setId(forwardUser.getId());
        trustedUser.setName(forwardUser.getFullName());
        return addUserToBd(message, messenger, trustedUser);
    }

    public boolean addUserToBd(MsgMessage message, Messenger messenger, Subscriber trustedUser) {
        if (trustedUserRepository.existsById(trustedUser.getId())) {
            messenger.sendText(message.getChatId(), "Пользователь " + trustedUser.getName() + " уже добавлен в список доверенных");
            return false;
        }
        if (!subscriberRepository.existsById(trustedUser.getId())) {
            subscriberRepository.save(trustedUser);
        }
        String pattern = """
                Вы дествительно хотете добавить пользователя
                ФИО: {0}
                UserId: {1}
                в список доверенных пользователей?""";


        messenger.sendText(message.getChatId(),
                MessageFormat.format(pattern, trustedUser.getName(), String.valueOf(trustedUser.getId())),
                getInlineBottoms(trustedUser.getId()));
        return true;
    }

    private Keyboard getInlineBottoms(String userId) {
        return Keyboard.row(Arrays.asList(
                getBean(AddUserCallBack.class).getInlineButton(String.valueOf(userId)),
                getBean(CancelAddUserCallBack.class).getInlineButton(String.valueOf(userId))));
    }
}