package com.cheremnov.bot.command.group_chess;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

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
    public void handleCommand(Message message, Bot bot) {
        bot.sendText(message.getChatId(), "Пришлите название группы турнира и количество туров в формате <Название группы тура> - <кол-во туров>.\n" +
                "Например:\n M11 - 9");
        bot.setMessageHandler(message.getChatId(), getBean(TournamentMessageHandler.class));
    }
}
