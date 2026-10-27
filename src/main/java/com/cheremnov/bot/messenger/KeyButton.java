package com.cheremnov.bot.messenger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Кнопка inline-клавиатуры: либо callback-кнопка, либо кнопка-ссылка.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyButton {

    private String text;

    /** Данные callback-кнопки (null для кнопки-ссылки). */
    private String callbackData;

    /** URL кнопки-ссылки (null для callback-кнопки). */
    private String url;

    public static KeyButton callbackButton(String text, String callbackData) {
        return new KeyButton(text, callbackData, null);
    }

    public static KeyButton linkButton(String text, String url) {
        return new KeyButton(text, null, url);
    }

    public boolean isLink() {
        return url != null;
    }
}