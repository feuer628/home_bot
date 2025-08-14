package com.cheremnov.bot;

import com.cheremnov.bot.callback.ICallbackHandler;
import com.cheremnov.bot.command.ICommandHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Handlers {

    @Getter
    private final List<ICommandHandler> commandHandlers;

    @Getter
    private final List<ICallbackHandler> callbackHandlers;

    // Использование конструктора для внедрения зависимостей
    @Autowired
    public Handlers(List<ICommandHandler> commandHandlers, List<ICallbackHandler> callbackHandlers) {
        this.commandHandlers = commandHandlers;
        this.callbackHandlers = callbackHandlers;
    }
}
