package com.cheremnov.bot.command.concert;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.command.concert.db.ConcertNumberRepository;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.HashSet;
import java.util.Set;

@Component
public class ConcertCallback implements ICallbackHandler {

    @Autowired
    TrustedUserRepository trustedUserRepository;

    @Autowired
    SubscriberRepository subscriberRepository;

    @Autowired
    ConcertNumberRepository concertNumberRepository;

    @Override
    public String callbackPrefix() {
        return "handle_concert_number";
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        Set<Long> trustedUsers = new HashSet<>();
        trustedUserRepository.findAll().forEach(trustedUser -> trustedUsers.add(trustedUser.getId()));
        int currentConcertNumber = Integer.parseInt(getCallbackInfo(callback));
        boolean hasNext = currentConcertNumber < concertNumberRepository.count() - 1;
        subscriberRepository.findAll().forEach(subscriber -> {
            if (subscriber.getChatId() != null) {
                bot.sendText(subscriber.getChatId(), getMessageText(currentConcertNumber, hasNext),
                        hasNext && trustedUsers.contains(subscriber.getId()) ? getSingleButton("Следующий номер", String.valueOf(currentConcertNumber + 1)) : null);
            }
        });
        bot.answerCallback(callback, null);
        bot.deleteInlineMarkup(callback.getMessage());
    }



    private String getMessageText(int currentConcertNumber, boolean hasNext) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\uD83C\uDFA4 Выступает \n ").append(concertNumberRepository.findById((long) (currentConcertNumber)).orElseThrow().getName());
        if (hasNext) {
            stringBuilder.append("\n").append("\n‼️ Следующий номер \n").append(concertNumberRepository.findById((long) (currentConcertNumber + 1)).orElseThrow().getName());
        }
        return stringBuilder.toString();
    }
}
