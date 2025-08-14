package com.cheremnov.bot.command;

import com.cheremnov.bot.message.AbstractMessageHandler;
import com.cheremnov.bot.Bot;
import com.cheremnov.bot.callback.ICallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;

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
            // TODO
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
            String pattern = """
                    Вы дествительно хотете добавить пользователя
                    ФИО: {0}
                    UserId: {1}
                    {2} в список доверенных пользователей?""";
            String fio = forwardUser.getFirstName() + " " + forwardUser.getLastName();
            String userName = forwardUser.getUserName() == null ?
                    "" : "UserName: @" + forwardUser.getUserName() + "\n";

            bot.sendText(message.getChatId(), MessageFormat.format(pattern, fio, forwardUser.getId().toString(), userName), getInlineBottoms());
        }

        private InlineKeyboardMarkup getInlineBottoms() {
            InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
            markupKeyboard.setKeyboard(Collections.singletonList(
                    Arrays.asList(getBean(AddUserCallBack.class).getInlineButton(), getBean(CancelAddUserCallBack.class).getInlineButton())));
            return markupKeyboard;
        }
    }
}
