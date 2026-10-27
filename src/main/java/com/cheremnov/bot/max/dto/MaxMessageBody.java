package com.cheremnov.bot.max.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * Содержимое сообщения (схема MessageBody).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaxMessageBody {

    /** Идентификатор сообщения (строка вида "mid.xxx"). */
    private String mid;

    private Long seq;

    private String text;

    private JsonNode attachments;
}