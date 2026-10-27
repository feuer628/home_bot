package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;

@Component
public class AddGroupTourCommand extends AbstractCommandHandler {

    @Override
    public String getCommandName() {
        return "add_group";
    }

    @Override
    public String getCommandDescription() {
        return "Добавление группы турнира";
    }

    @Override
    public void handleCommand(MsgMessage message, Messenger messenger) {
        messenger.sendText(message.getChatId(), "Пришлите название группы турнира и количество туров в формате <Название группы тура> - <кол-во туров> (допускается указание нескольких турниров с новой строки).\n" +
                "Например:\n M11 - 9");
        messenger.setMessageHandler(message.getChatId(), getBean(TournamentMessageHandler.class));
    }
}