package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.command.PageableModel;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class GroupTour implements PageableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer tourCount;

    private int currentTour;

    /**
     * Ключи чатов подписчиков вида "tg:123" / "mx:c123" / "mx:u123".
     */
    @ElementCollection
    private List<String> subscriberChatIds;

    public String getNameWithCount() {
        return name + " (" + tourCount + ")";
    }

    @Override
    public String getKey() {
        return String.valueOf(id);
    }
}