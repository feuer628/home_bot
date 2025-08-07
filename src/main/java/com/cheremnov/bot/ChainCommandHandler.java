package com.cheremnov.bot;

import com.cheremnov.bot.command.AbstractCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public class ChainCommandHandler {

    public static final String PREVIOUS_COMMAND_CLASS = "previousCommandName";
    private static final Map<Long, ChainCommandData> chainUserCommand = new HashMap<>();

    public static boolean hasChainCommand(Long userId) {
        return chainUserCommand.containsKey(userId);
    }

    public static AbstractCommand getChainUserCommand(Long userId, Update update) throws Exception {
        ChainCommandData chainCommandData = chainUserCommand.get(userId);
        return chainCommandData.nextCommand.
                getDeclaredConstructor(Update.class, Map.class).newInstance(update, chainCommandData.accumulatorData);
    }

    public static void setNextUserCommand(Long userId, Class<? extends AbstractCommand> nextCommand, Class<? extends AbstractCommand> previousCommandClass) {
        ChainCommandData chainCommandData = chainUserCommand.get(userId);
        // если это не первая комманда из цепочки, карту с данными берем из предыдущей команды
        Map<String, String> accumulatorData =
                chainCommandData == null ? new HashMap<>() : chainCommandData.accumulatorData;
        accumulatorData.put(PREVIOUS_COMMAND_CLASS, previousCommandClass.getName());
        chainUserCommand.put(userId, new ChainCommandData(nextCommand, accumulatorData));
    }

    public static void clearChainCommand(Long userId) {
        chainUserCommand.remove(userId);
    }

    public static class ChainCommandData {
        private final Class<? extends AbstractCommand> nextCommand;
        private final Map<String, String> accumulatorData;

        public <V, K> ChainCommandData(Class<? extends AbstractCommand> nextCommand, Map<String, String> accumulatorData) {
            this.nextCommand = nextCommand;
            this.accumulatorData = accumulatorData;
        }
    }
}
