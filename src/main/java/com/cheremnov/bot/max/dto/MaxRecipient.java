package com.cheremnov.bot.max.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Получатель сообщения (схема Recipient).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaxRecipient {

    private Long chat_id;

    private String chat_type;

    private Long user_id;

    private String post_id;
}