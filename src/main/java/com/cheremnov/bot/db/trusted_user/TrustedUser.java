package com.cheremnov.bot.db.trusted_user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;



@Entity
@Data
public class TrustedUser {

    @Id
    private long id;

    private String name;
}
