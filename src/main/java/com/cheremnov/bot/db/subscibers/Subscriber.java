package com.cheremnov.bot.db.subscibers;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Subscriber {

    /**
     * Ключ пользователя вида "tg:410563696" / "mx:123456".
     */
    @Id
    private String id;

    private String name;

    /**
     * Ключ чата для уведомлений вида "tg:123" / "mx:c123" / "mx:u123".
     */
    private String chatId;
}