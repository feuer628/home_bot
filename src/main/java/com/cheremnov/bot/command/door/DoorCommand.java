package com.cheremnov.bot.command.door;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;

@Component
public class DoorCommand extends AbstractCommandHandler {

    @Override
    public String getCommandName() {
        return "door";
    }

    @Override
    public String getCommandDescription() {
        return "Управление калиткой";
    }

    @Override
    public void handleCommand(MsgMessage message, Messenger messenger) {
        messenger.sendText(message.getChatId(), "Вы уверены, что хотите открыть калитку?",
                getBean(DoorCallback.class).getSingleButtonWithCancel("Открыть", null));
    }
}