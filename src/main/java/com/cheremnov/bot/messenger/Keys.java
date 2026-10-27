package com.cheremnov.bot.messenger;

/**
 * Утилиты для работы с ключами платформ.
 */
public final class Keys {

    private Keys() {
    }

    /**
     * Ключ чата Макса для диалога с пользователем: "mx:u&lt;userId&gt;".
     */
    public static String maxUserChatKey(long userId) {
        return Platform.MAX.getPrefix() + ":u" + userId;
    }

    /**
     * Ключ чата Макса для группового чата или канала: "mx:c&lt;chatId&gt;".
     */
    public static String maxChatKey(long chatId) {
        return Platform.MAX.getPrefix() + ":c" + chatId;
    }

    public static boolean isMaxUserChat(String chatKey) {
        return chatKey != null && chatKey.startsWith(Platform.MAX.getPrefix() + ":u");
    }

    public static boolean isMaxGroupChat(String chatKey) {
        return chatKey != null && chatKey.startsWith(Platform.MAX.getPrefix() + ":c");
    }

    /**
     * Числовая часть ключа Макса ("mx:u123" -> 123).
     */
    public static long maxRawId(String chatKey) {
        return Long.parseLong(Platform.rawId(chatKey).substring(1));
    }
}