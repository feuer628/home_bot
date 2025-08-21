package com.cheremnov.bot.command.door;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

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
    public void handleCommand(Message message, Bot bot) {
        bot.sendText(message.getChatId(), "Вы уверены, что хотите открыть калитку?", getBean(DoorCallback.class).getSingleButtonWithCancel("Открыть", null));
    }
}
