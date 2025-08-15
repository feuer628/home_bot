package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.ICommandHandler;
import com.cheremnov.bot.db.user.BotUser;
import com.cheremnov.bot.db.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class UserList implements ICommandHandler {

    @Autowired
    public UserRepository userRepository;

    @Override
    public String getCommandName() {
        return "user_list";
    }

    @Override
    public String getCommandDescription() {
        return "Список доверенных пользователей";
    }

    @Override
    public void handle(Message message, Bot bot) {
        StringBuilder stringBuilder = new StringBuilder();
        userRepository.findAll().forEach(botUser -> stringBuilder.append(botUser.getName()).append("\n"));
        bot.sendText(message.getChatId(), stringBuilder.toString());
    }
}
