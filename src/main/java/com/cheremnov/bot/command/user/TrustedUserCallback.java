package com.cheremnov.bot.command.user;

import com.cheremnov.bot.command.BackToListCallback;
import com.cheremnov.bot.messenger.Keyboard;
import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.db.trusted_user.TrustedUser;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import com.cheremnov.bot.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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
    public void handle(MsgCallback callback, Messenger messenger) {
        PaginationInfoModel paginationInfoModel = JsonUtils.objectFromString(getCallbackInfo(callback), PaginationInfoModel.class);
        TrustedUser trustedUser = trustedUserRepository.findById(paginationInfoModel.getEntityId()).orElseThrow();
        String text = "Доверенный пользователь:\n\n✅ " + trustedUser.getName();
        messenger.editMessageText(callback.getMessage().getChatId(), callback.getMessage().getMessageId(),
                text, getInlineKeyboard(paginationInfoModel));
    }

    private Keyboard getInlineKeyboard(PaginationInfoModel paginationInfoModel) {
        return Keyboard.row(Arrays.asList(
                context.getBean(BackToListCallback.class).getInlineButton("Назад", UserListCommand.class.getName(), String.valueOf(paginationInfoModel.getPNum())),
                context.getBean(DeleteUserCallback.class).getInlineButton(String.valueOf(paginationInfoModel.getEntityId()))));
    }
}