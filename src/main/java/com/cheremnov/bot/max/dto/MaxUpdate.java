package com.cheremnov.bot.max.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Событие Макса (схема Update).
 * Все подтипы полиморфны по полю update_type, поэтому используются
 * общие необязательные поля.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaxUpdate {

    /** message_created | message_callback | bot_started | ... */
    private String update_type;

    private Long timestamp;

    private MaxMessage message;

    private MaxCallback callback;

    private MaxUser user;

    private Long chat_id;

    private Boolean is_channel;

    private String message_id;

    private String user_locale;
}