package com.cheremnov.bot.command;

import com.cheremnov.bot.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Абстрактный класс-обработчик для обработки callback-запросов в Telegram-боте.
 * Предоставляет общую логику и интерфейс для реализации конкретных обработчиков,
 * связанных с пользовательскими действиями, например, нажатием на inline-кнопки.
 *
 * <p>Класс предназначен для упрощения создания новых обработчиков callback-запросов,
 * обеспечивая единую точку входа и возможность расширения функциональности через переопределение методов.</p>
 *
 * @author Cheremnov
 * @version 1.0
 */
@Component
public abstract class AbstractCallbackHandler implements ICallbackHandler {

    /**
     * Контекст Spring-приложения, используемый для получения бинов.
     * Внедряется автоматически с помощью аннотации {@link Autowired}.
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

    /**
     * Возвращает префикс callback-данных, который используется для определения,
     * какой обработчик должен обработать данный callback-запрос.
     *
     * @return строковый префикс callback-данных
     */
    @Override
    public abstract String callbackPrefix();

    /**
     * Основной метод для обработки callback-запроса.
     * Вызывает {@link #handleCallback(CallbackQuery, Bot)} и отправляет ответ на callback.
     *
     * @param callback объект CallbackQuery, полученный от Telegram API
     * @param bot      экземпляр бота, используемый для взаимодействия с Telegram
     */
    @Override
    public final void handle(CallbackQuery callback, Bot bot) {
        handleCallback(callback, bot);
        bot.answerCallback(callback);
    }

    /**
     * Абстрактный метод, который должен быть реализован в подклассах.
     * Содержит конкретную логику обработки callback-запроса.
     *
     * @param callback объект CallbackQuery, полученный от Telegram API
     * @param bot      экземпляр бота, используемый для взаимодействия с Telegram
     */
    public abstract void handleCallback(CallbackQuery callback, Bot bot);
}
