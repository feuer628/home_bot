package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractMessageHandler;
import com.cheremnov.bot.command.IMessageHandler;
import com.cheremnov.bot.db.user.BotUser;
import com.cheremnov.bot.db.user.UserRepository;
import com.cheremnov.bot.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
    public UserRepository userRepository;

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
        String fio = forwardUser.getFirstName() + " " + forwardUser.getLastName();
        String userName = forwardUser.getUserName() == null ?
                "" : "UserName: @" + forwardUser.getUserName() + "\n";

        BotUser botUser = new BotUser();
        botUser.setId(forwardUser.getId());
        botUser.setName(forwardUser.getUserName());

        if (userRepository.existsById(botUser.getId())) {
            bot.sendText(message.getChatId(), "Пользователь " + botUser.getName() + " уже добавлен в список доверенных");
            return;
        }

        String pattern = """
                Вы дествительно хотете добавить пользователя
                ФИО: {0}
                UserId: {1}
                {2} в список доверенных пользователей?""";


        bot.sendText(message.getChatId(), MessageFormat.format(pattern, fio, forwardUser.getId().toString(), userName), getInlineBottoms(botUser));
    }

    private InlineKeyboardMarkup getInlineBottoms(BotUser botUser) {
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(Collections.singletonList(
                Arrays.asList(getBean(AddUserCallBack.class).getInlineButton(JsonUtils.objectToString(botUser)), getBean(CancelAddUserCallBack.class).getInlineButton())));
        return markupKeyboard;
    }
}
