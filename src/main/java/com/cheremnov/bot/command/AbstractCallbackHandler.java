package com.cheremnov.bot.command;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Абстрактный класс-обработчик для обработки callback-запросов (нажатий на inline-кнопки).
 * Предоставляет общую логику и интерфейс для реализации конкретных обработчиков.
 *
 * @author Cheremnov
 * @version 1.0
 */
@Component
public abstract class AbstractCallbackHandler implements ICallbackHandler {

    /**
     * Контекст Spring-приложения, используемый для получения бинов.
     */
    @Autowired
    private ApplicationContext context;

    /**
     * Возвращает бин указанного типа из Spring-контекста.
     *
     * @param requiredType тип требуемого бина
     * @param <T>          обобщённый тип возвращаемого значения
     * @return бин указанного типа
     */
    public <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    @Override
    public abstract String callbackPrefix();

    /**
     * Основной метод для обработки callback-запроса.
     * Вызывает {@link #handleCallback(MsgCallback, Messenger)} и отправляет ответ на callback.
     */
    @Override
    public final void handle(MsgCallback callback, Messenger messenger) {
        handleCallback(callback, messenger);
        messenger.answerCallback(callback);
    }

    /**
     * Абстрактный метод с конкретной логикой обработки callback-запроса.
     */
    public abstract void handleCallback(MsgCallback callback, Messenger messenger);
}