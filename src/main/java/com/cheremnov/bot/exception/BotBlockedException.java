package com.cheremnov.bot.exception;

public class BotBlockedException extends RuntimeException {
    public BotBlockedException(Exception e) {
        super(e);
    }
}
