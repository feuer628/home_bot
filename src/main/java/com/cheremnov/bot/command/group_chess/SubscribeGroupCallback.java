package com.cheremnov.bot.command.group_chess;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.ArrayList;
import java.util.List;

@Component
public class SubscribeGroupCallback extends AbstractCallbackHandler {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public String callbackPrefix() {
        return "subscribe_group";
    }

    @Override
    public void handleCallback(CallbackQuery callback, Bot bot) {
        GroupTour groupTour = groupRepository.findById(Long.valueOf(getCallbackInfo(callback))).orElseThrow();
        List<Long> subscribers = groupTour.getSubscriberChatIds();
        if (subscribers == null) {
            subscribers = new ArrayList<>();
        }
        String sendText;
        if (subscribers.contains(callback.getMessage().getChatId())) {
            subscribers.remove(callback.getMessage().getChatId());
            sendText = "Вы успешно отписались от группы " + groupTour.getName();
        } else {
            subscribers.add(callback.getMessage().getChatId());
            sendText = "Вы успешно подписались на группу " + groupTour.getName();
        }
        groupTour.setSubscriberChatIds(subscribers);
        groupRepository.save(groupTour);
        bot.sendText(callback.getMessage().getChatId(), sendText);
        bot.deleteInlineMarkup(callback.getMessage());
    }
}
