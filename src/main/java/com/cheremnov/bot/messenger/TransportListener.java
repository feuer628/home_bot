package com.cheremnov.bot.messenger;

/**
 * Приём входящих событий от транспорта конкретной платформы.
 */
public interface TransportListener {

    void onMessage(MsgMessage message);

    void onCallback(MsgCallback callback);
}