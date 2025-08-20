package com.cheremnov.bot.command.concert.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ConcertNumber {
    @Id
    private Integer id;

    private String name;

}
