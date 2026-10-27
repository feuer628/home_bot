package com.cheremnov.bot.max;

/**
 * Ошибка запроса к API Макса.
 */
public class MaxApiException extends RuntimeException {

    private final int statusCode;

    public MaxApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public MaxApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
    }

    public int getStatusCode() {
        return statusCode;
    }
}