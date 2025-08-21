package com.cheremnov.bot.command.concert;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class AddConcertCommand extends AbstractCommandHandler {

    @Override
    public String getCommandName() {
        return "add_concert";
    }

    @Override
    public String getCommandDescription() {
        return "Добавление нового концерта";
    }

    @Override
    public void handleCommand(Message message, Bot bot) {
        bot.sendText(message.getChatId(), "Для добавления нового концерта пришлите список номеров, где каждый номер на отдельной строке. Старый концерт будет удален");
        bot.setMessageHandler(message.getChatId(), getBean(AddConcertMessageHandler.class));
    }
}
