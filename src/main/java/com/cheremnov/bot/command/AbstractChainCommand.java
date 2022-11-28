package com.cheremnov.bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class AbstractChainCommand extends AbstractCommand {

    protected Map<String, String> accumulatorData;
    protected Update update;

    public AbstractChainCommand(Update update, Map<String, String> accumulatorData) {
        this.accumulatorData = accumulatorData;
        this.update = update;
    }
}
