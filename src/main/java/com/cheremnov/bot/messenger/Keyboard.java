package com.cheremnov.bot.messenger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Платформо-независимая inline-клавиатура (двумерный массив кнопок).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Keyboard {

    private List<List<KeyButton>> rows = new ArrayList<>();

    public static Keyboard of(List<List<KeyButton>> rows) {
        return new Keyboard(rows);
    }

    public static Keyboard single(KeyButton button) {
        return new Keyboard(Collections.singletonList(Collections.singletonList(button)));
    }

    public static Keyboard row(KeyButton... buttons) {
        return new Keyboard(Collections.singletonList(Arrays.asList(buttons)));
    }

    /**
     * Клавиатура из одного ряда кнопок.
     */
    public static Keyboard row(List<KeyButton> buttons) {
        return new Keyboard(Collections.singletonList(buttons));
    }
}