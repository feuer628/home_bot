package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractMessageHandler;
import com.cheremnov.bot.db.trusted_user.TrustedUser;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import com.cheremnov.bot.utils.JsonUtils;
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

    @Override
    public void handle(Message message, Bot bot) {
        // тут должно быть пересланное сообщение
        User forwardUser = message.getForwardFrom();
        if (forwardUser == null) {
            bot.sendText(message.getChatId(), "Вы должны переслать сообщение от пользователя, которого хотите добавить в список доверенных");
            return;
        }
        if (forwardUser.getIsBot()) {
            bot.sendText(message.getChatId(), "Ботов нельзя добавлять в список доверенных");
            return;
        }
        String fio = String.join(" ", forwardUser.getFirstName(), forwardUser.getLastName());
        String userName = forwardUser.getUserName() == null ?
                "" : "UserName: @" + forwardUser.getUserName() + "\n";

        TrustedUser trustedUser = new TrustedUser();
        trustedUser.setId(forwardUser.getId());
        trustedUser.setName(fio);

        if (trustedUserRepository.existsById(trustedUser.getId())) {
            bot.sendText(message.getChatId(), "Пользователь " + trustedUser.getName() + " уже добавлен в список доверенных");
            return;
        }

        String pattern = """
                Вы дествительно хотете добавить пользователя
                ФИО: {0}
                UserId: {1}
                {2} в список доверенных пользователей?""";


        bot.sendText(message.getChatId(), MessageFormat.format(pattern, fio, forwardUser.getId().toString(), userName), getInlineBottoms(trustedUser));
    }

    private InlineKeyboardMarkup getInlineBottoms(TrustedUser trustedUser) {
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(Collections.singletonList(
                Arrays.asList(getBean(AddUserCallBack.class).getInlineButton(JsonUtils.objectToString(trustedUser)), getBean(CancelAddUserCallBack.class).getInlineButton())));
        return markupKeyboard;
    }
}
