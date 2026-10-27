package com.cheremnov.bot.command.alice;

import com.cheremnov.bot.messenger.KeyButton;
import com.cheremnov.bot.messenger.Keyboard;
import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.alice.Alice;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;

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
    public void handleCommand(MsgMessage message, Messenger messenger) {
        messenger.sendText(message.getChatId(), "Выберете куда хотите отправить сообщение", getInlineBottoms());
    }

    private Keyboard getInlineBottoms() {
        List<List<KeyButton>> keyboard = new ArrayList<>();
        for (Alice alice : Alice.values()) {
            keyboard.add(Collections.singletonList(getBean(SayAliceCallback.class).getInlineButton(alice.getName(), alice.name())));
        }
        keyboard.add(Collections.singletonList(getBean(SayAliceCallback.class).getInlineButton("На все", "all")));
        return Keyboard.of(keyboard);
    }
}