package com.cheremnov.bot.messenger;

/**
 * Платформа мессенджера.
 * Ключи идентификаторов (пользователей и чатов) самодостаточны и имеют вид
 * "&lt;префикс&gt;:&lt;id&gt;", например "tg:410563696" или "mx:u123456".
 * Это позволяет хранить ключи из разных мессенджеров в одних и тех же таблицах
 * и маршрутизировать отправку по префиксу.
 */
public enum Platform {

    TELEGRAM("tg"),
    MAX("mx");

    private final String prefix;

    Platform(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String userKey(long userId) {
        return prefix + ":" + userId;
    }

    public String userKey(String userId) {
        return prefix + ":" + userId;
    }

    public String chatKey(long chatId) {
        return prefix + ":" + chatId;
    }

    public boolean owns(String key) {
        return key != null && key.startsWith(prefix + ":");
    }

    /**
     * Возвращает платформу по ключу или null, если префикс неизвестен.
     */
    public static Platform fromKey(String key) {
        if (key == null) {
            return null;
        }
        for (Platform platform : values()) {
            if (platform.owns(key)) {
                return platform;
            }
        }
        return null;
    }

    /**
     * Возвращает идентификатор без префикса платформы.
     */
    public static String rawId(String key) {
        if (key == null) {
            return null;
        }
        int index = key.indexOf(':');
        return index >= 0 ? key.substring(index + 1) : key;
    }
}