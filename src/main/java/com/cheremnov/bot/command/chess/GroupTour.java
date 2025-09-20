package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.command.PageableModel;
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
    private List<Long> subscriberChatIds;

    public String getNameWithCount() {
        return name + " (" + tourCount + ")";
    }
}
