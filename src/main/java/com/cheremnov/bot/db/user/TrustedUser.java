package com.cheremnov.bot.db.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;



@Entity
@Data
public class TrustedUser {

    @Id
    private Long id;

    private String name;
}
