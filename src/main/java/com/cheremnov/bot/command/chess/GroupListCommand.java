package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.command.AbstractPagebleCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupListCommand extends AbstractPagebleCommand<GroupTour> {

    @Autowired
    public GroupListCommand(GroupRepository repository) {
        super(repository, GroupCallback.class);
    }

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
    public void handleCommand(MsgMessage message, Messenger messenger) {
        messenger.sendText(message.getChatId(), getCommandDescription(), getInlineKeyboard(0));
    }

}