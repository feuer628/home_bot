package com.cheremnov.bot.command.alice;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.alice.Alice;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class SayAliceCommand extends AbstractCommandHandler {

    @Override
    public String getCommandName() {
        return "say_alice";
    }

    @Override
    public String getCommandDescription() {
        return "Передать сообщение на алису";
    }

    @Override
    public void handleCommand(Message message, Bot bot) {
        bot.sendText(message.getChatId(), "Выберете куда хотите отправить сообщение", getInlineBottoms());
    }

    private InlineKeyboardMarkup getInlineBottoms() {
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (Alice alice : Alice.values()) {
            keyboard.add(Collections.singletonList(getBean(SayAliceCallback.class).getInlineButton(alice.getName(), alice.name())));
        }
        keyboard.add(Collections.singletonList(getBean(SayAliceCallback.class).getInlineButton("На все", "all")));
        markupKeyboard.setKeyboard(keyboard);
        return markupKeyboard;
    }
}
