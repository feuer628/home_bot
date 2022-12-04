package com.cheremnov.bot.command;

import com.cheremnov.bot.ChainCommandHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author cheremnov
 *         Date: 28.11.2022
 */
public class OnOffCommandHandler extends AbstractChainCommand {

    public OnOffCommandHandler(Update update, Map<String, String> accumulatorData) {
        super(update, accumulatorData);
    }

    @Override
    public void doAction(SendMessage message) {
        try {
            String action = switch (update.getMessage().getText()) {
                case "Включить" -> "on";
                case "Выключить" -> "off";
                default -> throw new IllegalStateException("Unexpected value: " + update.getMessage().getText());
            };
            Class<? extends AbstractOnOffCommand> onOffCommandClass = (Class<? extends AbstractOnOffCommand>) Class.forName(accumulatorData.get(ChainCommandHandler.PREVIOUS_COMMAND_CLASS));
            AbstractOnOffCommand onOffCommand = onOffCommandClass.getDeclaredConstructor(List.class).newInstance(Collections.singletonList(action));
            onOffCommand.parseAndCheckArgs();
            onOffCommand.doAction(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
