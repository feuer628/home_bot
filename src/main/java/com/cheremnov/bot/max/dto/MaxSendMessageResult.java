package com.cheremnov.bot.max.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Результат отправки сообщения (схема SendMessageResult).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaxSendMessageResult {

    private MaxMessage message;
}