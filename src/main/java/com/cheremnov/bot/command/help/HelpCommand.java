package com.cheremnov.bot.command.help;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class HelpCommand extends AbstractCommandHandler {
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
        return "";
    }

    @Override
    public void handleCommand(Message message, Bot bot) {
        bot.sendText(message.getChatId(), """
                Этот бот разработан для расчета цены, а также заказа автоматического мангала.
               
                Для расчета цены используйте команду /calc
                
                После расчета цены, Вы сможете заказать мангал.
                """);
    }
}
