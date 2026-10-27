package com.cheremnov.bot.max.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Нажатие на inline-кнопку (схема Callback).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaxCallback {

    private Long timestamp;

    /** Идентификатор callback-нажатия (передаётся в POST /answers). */
    private String callback_id;

    /** Данные кнопки. */
    private String payload;

    private MaxUser user;
}