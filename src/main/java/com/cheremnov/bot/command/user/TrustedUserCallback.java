package com.cheremnov.bot.command.user;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.db.trusted_user.TrustedUser;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import com.cheremnov.bot.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Arrays;
import java.util.Collections;

@Component
public class TrustedUserCallback implements ICallbackHandler {

    @Autowired
    ApplicationContext context;

    @Autowired
    TrustedUserRepository trustedUserRepository;

    @Override
    public String callbackPrefix() {
        return "trusted_user";
    }

    @Override
    public void handle(CallbackQuery callback, Bot bot) {
        PaginationInfoModel paginationInfoModel = JsonUtils.objectFromString(getCallbackInfo(callback), PaginationInfoModel.class);
        TrustedUser trustedUser = trustedUserRepository.findById(paginationInfoModel.getEntityId()).orElseThrow();
        String text = "Доверенный пользователь:\n\n✅ " + trustedUser.getName();
        bot.editMessageText(callback.getMessage().getChatId(), callback.getMessage().getMessageId(), text, getInlineKeyboard(paginationInfoModel));
    }

    private InlineKeyboardMarkup getInlineKeyboard(PaginationInfoModel paginationInfoModel) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(Arrays.asList(context.getBean(UserListCallback.class).getInlineButton("Назад", String.valueOf(paginationInfoModel.getPNum())),
                context.getBean(DeleteUserCallback.class).getInlineButton(String.valueOf(paginationInfoModel.getEntityId())))));
        return inlineKeyboardMarkup;
    }
}
