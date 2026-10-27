package com.cheremnov.bot.max.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Пользователь Макса (схема User).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaxUser {

    private Long user_id;

    private String first_name;

    private String last_name;

    private String username;

    private Boolean is_bot;
}