package com.cheremnov.bot.message;

import com.cheremnov.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface IMessageHandler {
    void handle(Message message, Bot bot);
}
