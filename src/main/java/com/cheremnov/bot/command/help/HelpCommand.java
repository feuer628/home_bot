package com.cheremnov.bot.command.help;

import com.cheremnov.bot.Handlers;
import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;

import java.util.Comparator;

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

    /**
     * Список доступных команд с описаниями.
     * Handlers берётся лениво через контекст, чтобы не создавать
     * кольцевую зависимость HelpCommand -> Handlers -> набор команд.
     */
    protected String getCommandsHelp() {
        StringBuilder sb = new StringBuilder("\nДоступные команды:\n");
        getBean(Handlers.class).getCommandHandlers().stream()
                .filter(handler -> !handler.isCommandHidden() && handler.getCommandDescription() != null)
                .sorted(Comparator.comparing(AbstractCommandHandler::getCommandName))
                .forEach(handler -> sb.append('/')
                        .append(handler.getCommandName())
                        .append(" — ")
                        .append(handler.getCommandDescription())
                        .append('\n'));
        return sb.toString();
    }

    @Override
    public void handleCommand(MsgMessage message, Messenger messenger) {
        messenger.sendText(message.getChatId(), HELP_TEXT + getCommandsHelp());
    }
}