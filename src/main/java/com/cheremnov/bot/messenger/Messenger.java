package com.cheremnov.bot.messenger;

import com.cheremnov.bot.command.AbstractMessageHandler;

/**
 * Платформо-независимый интерфейс взаимодействия с мессенджером.
 * Маршрутизация по платформе выполняется по префиксу ключа чата
 * (см. {@link Platform}).
 */
public interface Messenger {

    void sendText(String chatKey, String text);

    void sendText(String chatKey, String text, Keyboard keyboard);

    /**
     * Редактирует текст сообщения. Если keyboard == null, клавиатура убирается.
     */
    void editMessageText(String chatKey, String messageId, String text, Keyboard keyboard);

    /**
     * Устанавливает клавиатуру у существующего сообщения (keyboard == null — убирает).
     */
    void setInlineMarkup(String chatKey, String messageId, Keyboard keyboard);

    void deleteInlineMarkup(String chatKey, String messageId);

    void answerCallback(MsgCallback callback);

    void answerCallback(MsgCallback callback, String text);

    default void editMessageText(MsgMessage message, String text, Keyboard keyboard) {
        editMessageText(message.getChatId(), message.getMessageId(), text, keyboard);
    }

    default void setInlineMarkup(MsgMessage message, Keyboard keyboard) {
        setInlineMarkup(message.getChatId(), message.getMessageId(), keyboard);
    }

    default void deleteInlineMarkup(MsgMessage message) {
        setInlineMarkup(message.getChatId(), message.getMessageId(), null);
    }

    /**
     * Регистрирует обработчик текстовых сообщений для чата (диалоговые сценарии).
     */
    void setMessageHandler(String chatKey, AbstractMessageHandler handler);

    void deleteMessageHandler(String chatKey);
}