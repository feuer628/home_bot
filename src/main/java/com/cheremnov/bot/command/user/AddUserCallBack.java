package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.db.user.BotUser;
import com.cheremnov.bot.db.user.UserRepository;
import com.cheremnov.bot.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
@Slf4j
public class AddUserCallBack implements ICallbackHandler {

    @Autowired
    UserRepository userRepository;

    @Override
    public String callbackPrefix() {
        return "addUser";
    }

    @Override
    public InlineKeyboardButton getInlineButton() {
        return InlineKeyboardButton.builder().text("Добавить").callbackData(callbackPrefix()).build();
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        bot.deleteInlineMarkup(callback.getMessage());
        log.info(String.valueOf(userRepository.count()));
        BotUser botUser = JsonUtils.objectFromString(getCallbackInfo(callback), BotUser.class);
        userRepository.save(botUser);
        log.info(String.valueOf(userRepository.count()));
        bot.restoreDefaultMessageHandler();
        bot.sendText(callback.getMessage().getChatId(), "Пользователь добавлен в список доверенных");
        bot.answerCallback(callback, null);
    }
}
