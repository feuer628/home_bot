package com.cheremnov.bot.command.user;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.messenger.Platform;
import com.cheremnov.bot.command.AbstractCommandHandler;
import com.cheremnov.bot.db.subscibers.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    public void handleCommand(MsgMessage message, Messenger messenger) {
        if (hasText(message)) {
            Subscriber trustedUser = getUserInfo(messenger, message);
            if (trustedUser != null) {
                getBean(AddUserHandler.class).addUserToBd(message, messenger, trustedUser);
                return;
            }
        }
        messenger.sendText(message.getChatId(), "Для добавления пользователя в список доверенных перешлите любое его сообщение в этот чат");
        messenger.setMessageHandler(message.getChatId(), getBean(AddUserHandler.class));
    }

    /**
     * Получает юзера из сообщения
     *
     * @param message сообщение
     * @return пользователь или нул
     */
    private Subscriber getUserInfo(Messenger messenger, MsgMessage message) {
        try {
            String text = message.getText().substring(getCommandName().length() + 2);
            String userId = text.substring(0, text.indexOf(" "));
            String fio = text.substring(text.indexOf(" ")).trim();
            Subscriber trustedUser = new Subscriber();
            // ключ пользователя дополняем префиксом платформы полученного сообщения
            trustedUser.setId(platformUserKey(message, userId));
            trustedUser.setName(fio);
            return trustedUser;
        } catch (Exception e) {
            messenger.sendText(message.getChatId(), "Неправильный формат добавления пользователя.\n" +
                    "Для добавления пользователя сразу из команды используй такой формат:\n/" +
                    getCommandName() + " <id пользователя> <псевдоним пользователя>");
        }
        return null;
    }

    private String platformUserKey(MsgMessage message, String userId) {
        Platform platform = Platform.fromKey(message.getChatId());
        return platform == null ? userId : platform.userKey(userId);
    }
}