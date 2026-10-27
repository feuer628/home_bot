package com.cheremnov.bot.command;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.security.UserChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractCommandHandler {


    @Autowired
    private UserChecker userChecker;
    @Autowired
    private ApplicationContext context;

    public boolean isCommandHidden() {
        return false;
    }

    public boolean isPublicCommand() {
        return false;
    }

    public abstract String getCommandName();

    public abstract String getCommandDescription();

    public <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    public final void handle(MsgMessage message, Messenger messenger) {
        if (!isPublicCommand() && !userChecker.isUserTrusted(message.getFrom().getId())) {
            messenger.sendText(message.getChatId(), "Вы не имеете право на выполнение этой команды");
            return;
        }
        // если пришла команда, то удаляем обработчики старых сообщений
        messenger.deleteMessageHandler(message.getChatId());
        handleCommand(message, messenger);
    }

    public abstract void handleCommand(MsgMessage message, Messenger messenger);

    protected boolean hasText(MsgMessage message) {
        return message.hasText() && !message.getText().trim().equals("/" + getCommandName());
    }
}