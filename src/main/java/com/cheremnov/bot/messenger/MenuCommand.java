package com.cheremnov.bot.messenger;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Команда меню бота.
 */
@Data
@AllArgsConstructor
public class MenuCommand {

    private String name;

    private String description;
}