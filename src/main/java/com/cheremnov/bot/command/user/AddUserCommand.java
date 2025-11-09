package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import com.cheremnov.bot.db.subscibers.Subscriber;
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
        return "Добавление доверенного пользователя";
    }

    @Override
    public void handleCommand(Message message, Bot bot) {
        if (hasText(message)) {
            Subscriber trustedUser = getUserInfo(bot, message);
            if (trustedUser != null) {
                getBean(AddUserHandler.class).addUserToBd(message, bot, trustedUser);
                return;
            }
        }
        bot.sendText(message.getChatId(), "Для добавления пользователя в список доверенных перешлите любое его сообщение в этот чат");
        bot.setMessageHandler(message.getChatId(), getBean(AddUserHandler.class));
    }

    /**
     * Получает юзера из сообщения
     *
     * @param message сообщение
     * @return ИД пользователя или нул
     */
    private Subscriber getUserInfo(Bot bot, Message message) {
        try {
            String text = message.getText().substring(getCommandName().length() + 2);
            long userId = Long.parseLong(text.substring(0, text.indexOf(" ")));
            String fio = text.substring(text.indexOf(" ")).trim();
            Subscriber trustedUser = new Subscriber();
            trustedUser.setId(userId);
            trustedUser.setName(fio);
            return trustedUser;
        } catch (Exception e) {
            bot.sendText(message.getChatId(), "Неправильный формат добавления пользователя.\n" +
                    "Для добавления пользователя сразу из команды используй такой формат:\n/" +
                    getCommandName() + " <id пользователя> <псевдоним пользователя>");
        }
        return null;
    }
}
