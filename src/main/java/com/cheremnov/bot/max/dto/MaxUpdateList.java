package com.cheremnov.bot.max.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Страница обновлений (схема UpdateList).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaxUpdateList {

    private List<MaxUpdate> updates;

    /** Указатель на следующую страницу. */
    private Long marker;
}