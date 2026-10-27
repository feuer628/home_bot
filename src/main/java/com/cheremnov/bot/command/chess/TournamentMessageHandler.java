package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.command.AbstractMessageHandler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TournamentMessageHandler extends AbstractMessageHandler {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public boolean handleMessage(MsgMessage message, Messenger messenger) {

        List<String> tours = List.of(message.getText().split("\n"));
        AtomicBoolean isError = new AtomicBoolean(false);
        List<GroupTour> groupTours = new ArrayList<>();
        tours.forEach(tour -> {
            String[] tournament = tour.split("-");
            if (tournament.length != 2 || StringUtils.isBlank(tournament[0]) || StringUtils.isBlank(tournament[1])) {
                isError.set(true);
                return;
            }
            int tourCount = Integer.parseInt(tournament[1].trim());
            GroupTour groupTour = new GroupTour();
            groupTour.setTourCount(tourCount);
            groupTour.setName(tournament[0].trim());
            groupTours.add(groupTour);
        });
        if (isError.get()) {
            messenger.sendText(message.getChatId(), "Неверный формат сообщения");
            return false;
        }
        StringBuilder textToSend = new StringBuilder("Добавлены группы турнира: ");
        groupTours.forEach(groupTour -> {
                    textToSend.append(groupTour.getNameWithCount()).append("; ");
                    groupRepository.save(groupTour);
                }
        );

        messenger.sendText(message.getChatId(), textToSend.toString().trim());
        return true;
    }
}