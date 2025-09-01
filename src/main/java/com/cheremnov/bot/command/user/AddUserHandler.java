package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractMessageHandler;
import com.cheremnov.bot.db.subscibers.Subscriber;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;

@Component
public class AddUserHandler extends AbstractMessageHandler {

    @Autowired
    public TrustedUserRepository trustedUserRepository;

    @Autowired
    public SubscriberRepository subscriberRepository;

    @Override
    public boolean handleMessage(Message message, Bot bot) {
        // тут должно быть пересланное сообщение
        User forwardUser = message.getForwardFrom();
        if (forwardUser == null) {
            bot.sendText(message.getChatId(), "Вы должны переслать сообщение от пользователя, которого хотите добавить в список доверенных");
            return false;
        }
        if (forwardUser.getIsBot()) {
            bot.sendText(message.getChatId(), "Ботов нельзя добавлять в список доверенных");
            return false;
        }
        String fio = String.join(" ", forwardUser.getFirstName(), forwardUser.getLastName());
        String userName = forwardUser.getUserName() == null ?
                "" : "UserName: @" + forwardUser.getUserName() + "\n";
        Subscriber subscriber = new Subscriber();
        subscriber.setId(forwardUser.getId());
        subscriber.setName(fio);
        if (trustedUserRepository.existsById(subscriber.getId())) {
            bot.sendText(message.getChatId(), "Пользователь " + subscriber.getName() + " уже добавлен в список доверенных");
            return false;
        }
        if (!subscriberRepository.existsById(subscriber.getId())) {
            subscriberRepository.save(subscriber);
        }
        String pattern = """
                Вы дествительно хотете добавить пользователя
                ФИО: {0}
                UserId: {1}
                {2} в список доверенных пользователей?""";


        bot.sendText(message.getChatId(), MessageFormat.format(pattern, fio, forwardUser.getId().toString(), userName), getInlineBottoms(subscriber.getId()));
        return true;
    }

    private InlineKeyboardMarkup getInlineBottoms(long userId) {
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(Collections.singletonList(
                Arrays.asList(getBean(AddUserCallBack.class).getInlineButton(String.valueOf(userId)), getBean(CancelAddUserCallBack.class).getInlineButton(String.valueOf(userId)))));
        return markupKeyboard;
    }
}
