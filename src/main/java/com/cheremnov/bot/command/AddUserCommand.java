package com.cheremnov.bot.command;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.BotUser;
import com.cheremnov.bot.UserRepository;
import com.cheremnov.bot.callback.ICallbackHandler;
import com.cheremnov.bot.message.AbstractMessageHandler;
import com.cheremnov.bot.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Component
public class AddUserCommand extends AbstractCommandHandler {

    @Override
    public String getCommandName() {
        return "add_user";
    }

    @Override
    public String getCommandDescription() {
        return "Добавление пользователей";
    }

    @Override
    public void handle(Message message, Bot bot) {
        bot.sendText(message.getChatId(), "Для добавления пользователя в список доверенных перешлите любое его сообщение в этот чат");
        bot.setMessageHandler(new AddUserHandler());
    }

    @Component
    public static class AddUserCallBack implements ICallbackHandler {

        @Autowired
        UserRepository userRepository;

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
            log.info(String.valueOf(userRepository.count()));
            BotUser botUser = JsonUtils.objectFromString(getCallbackInfo(callback), BotUser.class);
            userRepository.save(botUser);
            log.info(String.valueOf(userRepository.count()));
            bot.restoreDefaultMessageHandler();
            bot.sendText(callback.getMessage().getChatId(), "Пользователь добавлен в список доверенных");
            bot.answerCallback(callback, null);
        }
    }

    @Component
    public static class CancelAddUserCallBack implements ICallbackHandler {

        @Override
        public String callbackPrefix() {
            return "cancelAddUser";
        }

        @Override
        public InlineKeyboardButton getInlineButton() {
            return InlineKeyboardButton.builder().text("Отмена").callbackData(callbackPrefix()).build();
        }

        @Override
        public void handle(CallbackQuery callback, Bot bot) {
            bot.deleteInlineMarkup(callback.getMessage());
            // TODO
            bot.restoreDefaultMessageHandler();
            bot.sendText(callback.getMessage().getChatId(), "Добавление пользователя отменено");
            bot.answerCallback(callback, null);
        }
    }

    private class AddUserHandler extends AbstractMessageHandler {

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
}
