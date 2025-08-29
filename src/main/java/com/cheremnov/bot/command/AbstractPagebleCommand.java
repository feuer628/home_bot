package com.cheremnov.bot.command;

import com.cheremnov.bot.command.user.PaginationInfoModel;
import com.cheremnov.bot.db.PageableRepository;
import com.cheremnov.bot.utils.JsonUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public abstract class AbstractPagebleCommand<T extends PageableModel> extends AbstractCommandHandler {

    private final PageableRepository<T> repository;
    private final Class<? extends ICallbackHandler> aClass;

    public AbstractPagebleCommand(PageableRepository<T> repository, Class<? extends ICallbackHandler> aClass) {
        this.repository = repository;
        this.aClass = aClass;
    }

    public InlineKeyboardMarkup getInlineKeyboard(int pageNumber) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        PageRequest pageRequest = PageRequest.of(pageNumber, 5);
        Page<T> pageableModel = repository.findAll(pageRequest);
        ICallbackHandler pageModelCallback = getBean(aClass);
        List<List<InlineKeyboardButton>> userButton = new ArrayList<>();
        pageableModel.forEach(pageModel -> {
            PaginationInfoModel paginationInfoModel = new PaginationInfoModel();
            paginationInfoModel.setEntityId(pageModel.getId());
            paginationInfoModel.setPNum(pageRequest.getPageNumber());
            userButton.add(Collections.singletonList(
                    pageModelCallback.getInlineButton(
                            pageModel.getName() == null ? "???" : pageModel.getName(),
                            JsonUtils.objectToString(paginationInfoModel))));
        });
        List<InlineKeyboardButton> navButton = new ArrayList<>();
        BackToListCallback callback = getBean(BackToListCallback.class);
        if (pageNumber > 0) {
            navButton.add(callback.getInlineButton("◀️ Назад", this.getClass().getName(), String.valueOf(pageNumber - 1)));
        }
        if (pageNumber < pageableModel.getTotalPages() - 1) {
            navButton.add(callback.getInlineButton("Далее ▶️", this.getClass().getName(), String.valueOf(pageNumber + 1)));
        }
        if (!navButton.isEmpty()) {
            userButton.add(navButton);
        }
        inlineKeyboardMarkup.setKeyboard(userButton);
        return inlineKeyboardMarkup;
    }

}
