package com.cheremnov.bot.db.trusted_user;

import com.cheremnov.bot.command.PageableModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;



@Entity
@Data
public class TrustedUser implements PageableModel {

    /**
     * Ключ пользователя вида "tg:410563696" / "mx:123456".
     */
    @Id
    private String id;

    private String name;

    @Override
    public String getKey() {
        return id;
    }
}