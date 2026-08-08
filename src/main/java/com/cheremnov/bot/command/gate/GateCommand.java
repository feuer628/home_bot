package com.cheremnov.bot.command.gate;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

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
    public void handleCommand(Message message, Bot bot) {
        bot.sendText(message.getChatId(), "Выберете действие", getBean(GateCallback.class).getGateKeyboard());
    }
}
