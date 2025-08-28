package com.cheremnov.bot.command.group_chess;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import com.cheremnov.bot.command.user.PaginationInfoModel;
import com.cheremnov.bot.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GroupListCommand extends AbstractCommandHandler {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public boolean isPublicCommand() {
        return true;
    }

    @Override
    public String getCommandName() {
        return "group_list";
    }

    @Override
    public String getCommandDescription() {
        return "Список групп турнира";
    }

    @Override
    public void handleCommand(Message message, Bot bot) {
        bot.sendText(message.getChatId(), getCommandDescription(), getInlineKeyboard(0));
    }


    public InlineKeyboardMarkup getInlineKeyboard(int pageNumber) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        PageRequest pageRequest = PageRequest.of(pageNumber, 5);
        Page<GroupTour> groups = groupRepository.findAll(pageRequest);

        GroupCallback groupCallback = getBean(GroupCallback.class);
        List<List<InlineKeyboardButton>> groupButton = new ArrayList<>();
        groups.forEach(groupTour -> {
            PaginationInfoModel paginationInfoModel = new PaginationInfoModel();
            paginationInfoModel.setEntityId(groupTour.getId());
            paginationInfoModel.setPNum(pageRequest.getPageNumber());
            groupButton.add(Collections.singletonList(
                    groupCallback.getInlineButton(
                            groupTour.getName(),
                            JsonUtils.objectToString(paginationInfoModel))));
        });
        List<InlineKeyboardButton> navButton = new ArrayList<>();
        GroupListCallback callback = getBean(GroupListCallback.class);
        if (pageNumber > 0) {
            navButton.add(callback.getInlineButton("◀️ Назад", String.valueOf(pageNumber - 1)));
        }
        if (pageNumber < groups.getTotalPages() - 1) {
            navButton.add(callback.getInlineButton("Далее ▶️", String.valueOf(pageNumber + 1)));
        }
        if (!navButton.isEmpty()) {
            groupButton.add(navButton);
        }
        inlineKeyboardMarkup.setKeyboard(groupButton);
        return inlineKeyboardMarkup;
    }
}
