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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        String fio = Stream.of(forwardUser.getFirstName(), forwardUser.getLastName()).filter(Objects::nonNull).collect(Collectors.joining(" "));
        Subscriber trustedUser = new Subscriber();
        trustedUser.setId(forwardUser.getId());
        trustedUser.setName(fio);
        return addUserToBd(message, bot, trustedUser);
    }

    public boolean addUserToBd(Message message, Bot bot, Subscriber trustedUser) {
        if (trustedUserRepository.existsById(trustedUser.getId())) {
            bot.sendText(message.getChatId(), "Пользователь " + trustedUser.getName() + " уже добавлен в список доверенных");
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


        bot.sendText(message.getChatId(), MessageFormat.format(pattern, trustedUser.getName(), String.valueOf(trustedUser.getId())), getInlineBottoms(trustedUser.getId()));
        return true;
    }

    private InlineKeyboardMarkup getInlineBottoms(long userId) {
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(Collections.singletonList(
                Arrays.asList(getBean(AddUserCallBack.class).getInlineButton(String.valueOf(userId)), getBean(CancelAddUserCallBack.class).getInlineButton(String.valueOf(userId)))));
        return markupKeyboard;
    }
}
