package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
public class AddUserCommand extends AbstractCommandHandler {

    @Override
    public String getCommandName() {
        return "add_user";
    }

    @Override
    public String getCommandDescription() {
        return "Добавление пользователей";
    }

    @Override
    public void handle(Message message, Bot bot) {
        bot.sendText(message.getChatId(), "Для добавления пользователя в список доверенных перешлите любое его сообщение в этот чат");
        bot.setMessageHandler(getBean(AddUserHandler.class));
    }
}
