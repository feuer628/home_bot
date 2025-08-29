package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class TournamentMessageHandler extends AbstractMessageHandler {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public void handleMessage(Message message, Bot bot) {
        String text = message.getText();
        String[] tournament = text.split("-");
        if (tournament.length != 2) {
            bot.sendText(message.getChatId(), "Неверный формат сообщения");
            throw new RuntimeException("Неверный формат сообщения");
        }
        int tourCount = Integer.parseInt(tournament[1].trim());
        GroupTour groupTour = new GroupTour();
        groupTour.setTourCount(tourCount);
        groupTour.setName(tournament[0].trim());
        groupRepository.save(groupTour);
        bot.sendText(message.getChatId(), "Добавлена группа турнира: " + groupTour.getName());
    }
}
