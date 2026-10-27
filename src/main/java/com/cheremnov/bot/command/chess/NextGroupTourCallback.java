package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NextGroupTourCallback extends AbstractCallbackHandler {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public String callbackPrefix() {
        return "next_tour";
    }

    @Override
    public void handleCallback(MsgCallback callback, Messenger messenger) {
        String[] params = getCallbacksInfo(callback);
        long tourId = Long.parseLong(params[0]);
        String pageNumber = params[1];
        GroupTour groupTour = groupRepository.findById(tourId).orElseThrow();
        int currentTour = groupTour.getCurrentTour() + 1;
        groupTour.setCurrentTour(currentTour);
        String s = "В группе " + groupTour.getName() + " начался ";
        if (currentTour >= groupTour.getTourCount()) {
            s = s + "последний ";
        }
        String finalS = s;
        List<String> subscriberChatIds = groupTour.getSubscriberChatIds();
        if (subscriberChatIds != null) {
            subscriberChatIds.forEach(chatId -> messenger.sendText(chatId, finalS + currentTour + " тур"));
        }
        groupRepository.save(groupTour);
        callback.setData(callbackPrefix() + ":" + pageNumber);
        getBean(GroupListCallback.class).handleCallback(callback, messenger);
    }
}