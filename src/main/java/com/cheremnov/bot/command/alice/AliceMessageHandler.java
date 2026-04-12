package com.cheremnov.bot.command.alice;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.alice.Alice;
import com.cheremnov.bot.alice.AliceNotificationService;
import com.cheremnov.bot.command.AbstractMessageHandler;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class AliceMessageHandler extends AbstractMessageHandler {

    @Setter
    private String aliceId;

    @Autowired
    private AliceNotificationService aliceNotificationService;

    @Override
    public boolean handleMessage(Message message, Bot bot) {
        String msg = message.getText();
        if ("all".equals(aliceId)) {
            aliceNotificationService.sendToAllAlices(msg);
        } else {
            Alice alice = Alice.valueOf(aliceId);
            aliceNotificationService.sendToSpecificAlice(alice.getId(), msg);
        }
        return true;
    }
}
