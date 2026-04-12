package com.cheremnov.bot.command.alice;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class SayAliceCallback extends AbstractCallbackHandler {

    @Override
    public String callbackPrefix() {
        return "say";
    }

    @Override
    public void handleCallback(CallbackQuery callback, Bot bot) {
        bot.sendText(callback.getMessage().getChatId(), "Отправьте в этот чат сообщение, которое хотите передать Алисе");
        AliceMessageHandler messageHandler = getBean(AliceMessageHandler.class);
        messageHandler.setAliceId(getCallbackInfo(callback));
        bot.setMessageHandler(callback.getMessage().getChatId(), messageHandler);
        bot.deleteInlineMarkup(callback.getMessage());
    }
}
