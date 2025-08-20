package com.cheremnov.bot.command.concert;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractMessageHandler;
import com.cheremnov.bot.command.IMessageHandler;
import com.cheremnov.bot.command.concert.db.ConcertNumber;
import com.cheremnov.bot.command.concert.db.ConcertNumberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AddConcertMessageHandler extends AbstractMessageHandler {

    @Autowired
    ConcertNumberRepository concertNumberRepository;

    @Override
    public void handle(Message message, Bot bot) {
        concertNumberRepository.deleteAll();
        List<String> concertNumbers = List.of(message.getText().split("\n"));
        AtomicInteger i = new AtomicInteger(0);
        concertNumbers.forEach(concertNumber -> {
            ConcertNumber concertNum = new ConcertNumber();
            concertNum.setId(i.getAndIncrement());
            concertNum.setName(concertNumber);
            concertNumberRepository.save(concertNum);
        });
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(Collections.singletonList(getBean(ConcertCallback.class).getInlineButton("Старт ▶️", String.valueOf(0)))));
        bot.sendText(message.getChatId(), "Концерт добавлен, для его начала нажмите \"Старт ▶️\"", inlineKeyboardMarkup);
        bot.restoreDefaultMessageHandler();
    }
}
