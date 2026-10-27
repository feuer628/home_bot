package com.cheremnov.bot.max.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Пересланное или цитируемое сообщение (схема LinkedMessage).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaxLinkedMessage {

    /** forward | reply */
    private String type;

    private MaxUser sender;

    private Long chat_id;

    private MaxMessageBody message;
}