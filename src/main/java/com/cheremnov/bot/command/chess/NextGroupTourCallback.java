package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

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
    public void handleCallback(CallbackQuery callback, Bot bot) {
        GroupTour groupTour = groupRepository.findById(Long.valueOf(getCallbackInfo(callback))).orElseThrow();
        int currentTour = groupTour.getCurrentTour() + 1;
        groupTour.setCurrentTour(currentTour);
        String s = "В группе " + groupTour.getName() + " начался ";
        if (currentTour >= groupTour.getTourCount()) {
            s = s + "последний ";
        }
        String finalS = s;
        List<Long> subscriberChatIds = groupTour.getSubscriberChatIds();
        if (subscriberChatIds != null) {
            subscriberChatIds.forEach(chatId -> bot.sendText(chatId, finalS + currentTour + " тур"));
        }
        if (currentTour >= groupTour.getTourCount()) {
            groupRepository.deleteById(groupTour.getId());
        } else {
            groupRepository.save(groupTour);
        }
        bot.deleteInlineMarkup(callback.getMessage());
    }
}
