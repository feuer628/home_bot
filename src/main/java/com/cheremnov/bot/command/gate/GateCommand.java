package com.cheremnov.bot.command.gate;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;

@Component
public class GateCommand extends AbstractCommandHandler {

    @Override
    public String getCommandName() {
        return "gate";
    }

    @Override
    public String getCommandDescription() {
        return "Управление воротами";
    }

    @Override
    public void handleCommand(MsgMessage message, Messenger messenger) {
        messenger.sendText(message.getChatId(), "Выберете действие", getBean(GateCallback.class).getGateKeyboard());
    }
}