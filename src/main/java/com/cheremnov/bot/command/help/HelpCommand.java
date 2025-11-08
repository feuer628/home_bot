package com.cheremnov.bot.command.help;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class HelpCommand extends AbstractCommandHandler {

    protected static final String HELP_TEXT = """
            Этот бот разработан для уведомления участников турниров и их сопровождающих о начале тура в каждой группе.
            
            Для получения списка групп используйте команду /group_list
            
            Выберете интересующую Вас группу и подпишитесь на уведомления для этой группы.
            После этого Вы будете получать уведомления о начале каждого тура, а также приглашение на награждение
            """;

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public boolean isCommandHidden() {
        return true;
    }

    @Override
    public boolean isPublicCommand() {
        return true;
    }

    @Override
    public String getCommandDescription() {
        return null;
    }

    @Override
    public void handleCommand(Message message, Bot bot) {
        bot.sendText(message.getChatId(), HELP_TEXT);
    }
}
