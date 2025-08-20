package com.cheremnov.bot.db.subscibers;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Subscriber {

    @Id
    private long id;

    private String name;

    private long chatId;
}
