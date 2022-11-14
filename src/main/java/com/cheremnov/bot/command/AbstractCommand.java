package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCommand {

    List<Long> trustedUsers = new LinkedList<>();

    long SUPER_ADMIN_ID = 410563696;

    public void checkRight() {

    }


    public SendMessage doActionAndGetMessage(Message message) {
        System.out.println(message);
        String message1 = message.getText();
        System.out.println(message1);

        return new SendMessage(message.getChatId().toString(), message1);
    }


    private InlineKeyboardMarkup setInline() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("Кнопка");
        button.setCallbackData("skdfjhsdkfjshfkjshfkjhskdfjhskjfdhk");
        buttons1.add(button);
        buttons.add(buttons1);

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);
        return markupKeyboard;
    }
}
