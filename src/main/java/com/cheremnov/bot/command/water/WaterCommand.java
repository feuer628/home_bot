package com.cheremnov.bot.command.water;

import com.cheremnov.bot.messenger.Keyboard;
import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class WaterCommand extends AbstractCommandHandler {


    @Override
    public String getCommandName() {
        return "water";
    }

    @Override
    public String getCommandDescription() {
        return "Управление водой";
    }

    private Keyboard getInlineBottomOnOff() {
        return Keyboard.row(Arrays.asList(
                getBean(WaterOffCallback.class).getInlineButton(),
                getBean(WaterOnCallback.class).getInlineButton()));
    }

    @Override
    public void handleCommand(MsgMessage message1, Messenger messenger) {
        messenger.sendText(message1.getChatId(), "Что нужно сделать с водой?", getInlineBottomOnOff());
    }
}