package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import com.cheremnov.bot.db.user.TrustedUser;
import com.cheremnov.bot.db.user.TrustedUserRepository;
import com.cheremnov.bot.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class UserListCommand extends AbstractCommandHandler {

    @Autowired
    ApplicationContext context;

    @Autowired
    public TrustedUserRepository trustedUserRepository;

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
        bot.sendText(message.getChatId(), getCommandDescription(), getInlineKeyboard(0));
    }



    public InlineKeyboardMarkup getInlineKeyboard(int pageNumber) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        PageRequest pageRequest = PageRequest.of(pageNumber, 5);
        Page<TrustedUser> trustedUsers = trustedUserRepository.findAll(pageRequest);
        TrustedUserCallback trustedUserCallback = getBean(TrustedUserCallback.class);
        List<List<InlineKeyboardButton>> userButton = new ArrayList<>();
        trustedUsers.forEach(trustedUser -> {
            TrustedUserInfoModel trustedUserInfoModel = new TrustedUserInfoModel();
            trustedUserInfoModel.setUserId(trustedUser.getId());
            trustedUserInfoModel.setPNum(pageRequest.getPageNumber());
            userButton.add(Collections.singletonList(
                trustedUserCallback.getInlineButton(
                        trustedUser.getName() == null ? "???" : trustedUser.getName(),
                        JsonUtils.objectToString(trustedUserInfoModel))));
        });
        List<InlineKeyboardButton> navButton = new ArrayList<>();
        UserListCallback callback = context.getBean(UserListCallback.class);
        if (pageNumber > 0) {
            navButton.add(callback.getInlineButton("◀\uFE0F Назад", String.valueOf(pageNumber-1)));
        }
        long usersCount = trustedUserRepository.count();
        int startIndex = pageNumber * 5;
        int endIndex = (int) Math.min(startIndex + 5, usersCount);
        if ((endIndex < usersCount)) {
            navButton.add(callback.getInlineButton("Далее ▶\uFE0F", String.valueOf(pageNumber+1)));
        }
        if (!navButton.isEmpty()) {
            userButton.add(navButton);
        }
        inlineKeyboardMarkup.setKeyboard(userButton);
        return inlineKeyboardMarkup;
    }
}
