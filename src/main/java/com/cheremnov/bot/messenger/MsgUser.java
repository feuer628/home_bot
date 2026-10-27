package com.cheremnov.bot.messenger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Платформо-независимое представление пользователя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MsgUser {

    /** Ключ пользователя вида "tg:123" / "mx:456". */
    private String id;

    private String firstName;

    private String lastName;

    private boolean bot;

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            sb.append(firstName.trim());
        }
        if (lastName != null && !lastName.isBlank()) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(lastName.trim());
        }
        return sb.length() > 0 ? sb.toString() : "???";
    }
}