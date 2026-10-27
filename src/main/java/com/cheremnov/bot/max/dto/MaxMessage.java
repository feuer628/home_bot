package com.cheremnov.bot.max.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Сообщение в чате (схема Message).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaxMessage {

    private MaxUser sender;

    private MaxRecipient recipient;

    private Long timestamp;

    private MaxLinkedMessage link;

    private MaxMessageBody body;

    private String url;
}