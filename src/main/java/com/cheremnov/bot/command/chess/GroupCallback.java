package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.messenger.KeyButton;
import com.cheremnov.bot.messenger.Keyboard;
import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import com.cheremnov.bot.command.AbstractCallbackHandler;
import com.cheremnov.bot.command.user.PaginationInfoModel;
import com.cheremnov.bot.security.UserChecker;
import com.cheremnov.bot.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void handleCallback(MsgCallback callback, Messenger messenger) {
        PaginationInfoModel paginationInfoModel = JsonUtils.objectFromString(getCallbackInfo(callback), PaginationInfoModel.class);
        GroupTour groupTour = groupRepository.findById(Long.valueOf(paginationInfoModel.getEntityId())).orElseThrow();
        String text = "Группа: " + groupTour.getName();
        List<String> chatIds = groupTour.getSubscriberChatIds();
        if (chatIds == null) {
            chatIds = new ArrayList<>();
        }
        messenger.editMessageText(callback.getMessage().getChatId(), callback.getMessage().getMessageId(), text,
                getInlineKeyboard(callback.getFrom().getId(), paginationInfoModel,
                        chatIds.contains(callback.getMessage().getChatId()), groupTour));
    }

    private Keyboard getInlineKeyboard(String userKey, PaginationInfoModel paginationInfoModel, boolean isSubscribe, GroupTour groupTour) {
        int tour = groupTour.getCurrentTour() + 1;
        long groupId = groupTour.getId();

        List<List<KeyButton>> keyboard = new ArrayList<>();
        keyboard.add(Arrays.asList(
                getBean(GroupListCallback.class).getInlineButton("Назад", String.valueOf(paginationInfoModel.getPNum())),
                getBean(SubscribeGroupCallback.class).getInlineButton(isSubscribe ? "Отписаться" : "Подписаться", String.valueOf(paginationInfoModel.getEntityId()))));
        if (userChecker.isUserTrusted(userKey)) {
            keyboard.add(Collections.singletonList(getBean(SendMessageToGroupCallback.class).getInlineButton("Отправить сообщение в группу", String.valueOf(groupId))));
            keyboard.add(Collections.singletonList(getBean(DeleteGroupCallback.class).getInlineButton("Удалить группу", String.valueOf(groupId))));
            if (groupTour.getCurrentTour() < groupTour.getTourCount()) {
                keyboard.add(Collections.singletonList(getBean(NextGroupTourCallback.class).getInlineButton("Объявить начало " + tour + " тура", String.valueOf(paginationInfoModel.getEntityId()), String.valueOf(paginationInfoModel.getPNum()))));
            }
        }

        return Keyboard.of(keyboard);
    }
}