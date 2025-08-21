package com.cheremnov.bot.command;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.security.UserChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

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

    public final void handle(Message message, Bot bot) {
        if (!isPublicCommand()&& !userChecker.isUserTrusted(message.getFrom().getId())) {
            bot.sendText(message.getChatId(), "Вы не имеете право на выполнение этой команды");
            return;
        }
        handleCommand(message, bot);
    }

    public abstract void handleCommand(Message message, Bot bot);
}
