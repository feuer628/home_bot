package com.cheremnov.bot.db.trusted_user;

import com.cheremnov.bot.command.PageableModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;



@Entity
@Data
public class TrustedUser implements PageableModel {

    @Id
    private Long id;

    private String name;
}
