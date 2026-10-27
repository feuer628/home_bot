package com.cheremnov.bot.messenger;

import java.util.List;

/**
 * Транспорт конкретной платформы (Telegram, Макс).
 * Преобразует платформенные события в платформо-независимые модели
 * и выполняет исходящие запросы к API платформы.
 */
public interface MessageTransport {

    Platform platform();

    boolean isEnabled();

    void setListener(TransportListener listener);

    void sendText(String chatKey, String text, Keyboard keyboard);

    /**
     * Редактирует текст сообщения. keyboard == null — убрать клавиатуру.
     */
    void editMessageText(String chatKey, String messageId, String text, Keyboard keyboard);

    /**
     * Меняет только клавиатуру сообщения. keyboard == null — убрать клавиатуру.
     */
    void setInlineMarkup(String chatKey, String messageId, Keyboard keyboard);

    /**
     * Ответ на callback-нажатие (text == null — без уведомления).
     */
    void answerCallback(String callbackId, String text);

    void setCommands(List<MenuCommand> commands);

    /**
     * Ключ собственного пользователя бота ("mx:123"), если известен.
     * Используется для распознавания служебных команд, отправленных самим ботом.
     */
    default String selfUserKey() {
        return null;
    }
}