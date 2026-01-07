package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
public class CancelGroupTourCallback extends AbstractCallbackHandler {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public String callbackPrefix() {
        return "cancel_tour";
    }

    @Override
    public void handleCallback(CallbackQuery callback, Bot bot) {
        String[] params = getCallbacksInfo(callback);
        long tourId = Long.parseLong(params[0]);
        String pageNumber = params[1];
        GroupTour groupTour = groupRepository.findById(tourId).orElseThrow();
        int currentTour = groupTour.getCurrentTour() - 1;
        groupTour.setCurrentTour(currentTour);
        groupRepository.save(groupTour);
        callback.setData(callbackPrefix() + ":" + pageNumber);
        getBean(GroupListCallback.class).handleCallback(callback, bot);
    }
}
