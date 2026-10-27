package com.cheremnov.bot.command;

public interface PageableModel {

    /**
     * Ключ сущности для callback-данных: строковый ключ пользователя
     * или строковое представление числового идентификатора.
     */
    String getKey();

    String getName();
}