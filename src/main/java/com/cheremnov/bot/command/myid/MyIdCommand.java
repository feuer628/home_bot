package com.cheremnov.bot.command.myid;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.messenger.Platform;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;

/**
 * Отправителю показывает его идентификаторы (нужны, например,
 * для bot.super-admins в application.yml).
 */
@Component
public class MyIdCommand extends AbstractCommandHandler {

    @Override
    public String getCommandName() {
        return "myid";
    }

    @Override
    public String getCommandDescription() {
        return "Мой идентификатор пользователя";
    }

    @Override
    public boolean isPublicCommand() {
        return true;
    }

    @Override
    public void handleCommand(MsgMessage message, Messenger messenger) {
        String userKey = message.getFrom() == null ? null : message.getFrom().getId();
        String text = """
                Ваш ID: {0}
                Ключ пользователя (для bot.super-admins): {1}
                Ключ чата: {2}""";
        messenger.sendText(message.getChatId(), java.text.MessageFormat.format(text,
                userKey == null ? "???" : Platform.rawId(userKey),
                userKey == null ? "???" : userKey,
                message.getChatId()));
    }
}