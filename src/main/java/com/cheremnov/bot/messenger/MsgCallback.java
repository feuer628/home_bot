package com.cheremnov.bot.messenger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Платформо-независимое представление нажатия на inline-кнопку (callback).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MsgCallback {

    /** Идентификатор callback-запроса (платформо-специфичный). */
    private String id;

    /** Сообщение, к которому прикреплена клавиатура. */
    private MsgMessage message;

    /** Данные кнопки (callback payload). */
    private String data;

    /** Пользователь, нажавший кнопку. */
    private MsgUser from;
}