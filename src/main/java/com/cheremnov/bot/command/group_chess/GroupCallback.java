package com.cheremnov.bot.command.group_chess;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.command.user.DeleteUserCallback;
import com.cheremnov.bot.command.user.PaginationInfoModel;
import com.cheremnov.bot.command.user.UserListCallback;
import com.cheremnov.bot.db.trusted_user.TrustedUser;
import com.cheremnov.bot.security.UserChecker;
import com.cheremnov.bot.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class GroupCallback extends AbstractCallbackHandler {

    @Autowired
    private UserChecker userChecker;

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public String callbackPrefix() {
        return "group";
    }

    @Override
    public void handleCallback(CallbackQuery callback, Bot bot) {
        PaginationInfoModel paginationInfoModel = JsonUtils.objectFromString(getCallbackInfo(callback), PaginationInfoModel.class);
        GroupTour groupTour = groupRepository.findById(paginationInfoModel.getEntityId()).orElseThrow();
        String text = "Группа: " + groupTour.getName();
        List<Long> chatIds = groupTour.getSubscriberChatIds();
        if (chatIds == null) {
            chatIds = new ArrayList<>();
        }
        bot.editMessageText(callback.getMessage().getChatId(), callback.getMessage().getMessageId(), text, getInlineKeyboard(callback.getFrom().getId(), paginationInfoModel, chatIds.contains(callback.getMessage().getChatId())));
    }

    private InlineKeyboardMarkup getInlineKeyboard(Long userId, PaginationInfoModel paginationInfoModel, boolean isSubscribe) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add( Arrays.asList(
                getBean(GroupListCallback.class).getInlineButton("Назад", String.valueOf(paginationInfoModel.getPNum())),
                getBean(SubscribeGroupCallback.class).getInlineButton(isSubscribe ? "Отписаться" : "Подписаться", String.valueOf(paginationInfoModel.getEntityId()))));
        if (userChecker.isUserTrusted(userId)) {
            keyboard.add(Collections.singletonList(getBean(NextGroupTourCallback.class).getInlineButton("Объявить начало следующего тура", String.valueOf(paginationInfoModel.getEntityId()))));
        }

        return new InlineKeyboardMarkup(keyboard);
    }
}
