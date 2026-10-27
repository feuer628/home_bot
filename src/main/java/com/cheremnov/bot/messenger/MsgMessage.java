package com.cheremnov.bot.messenger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Платформо-независимое представление входящего сообщения.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MsgMessage {

    /** Ключ чата вида "tg:123" / "mx:c123" / "mx:u123". */
    private String chatId;

    /** Идентификатор сообщения (платформо-специфичный: "123" для Telegram, "mid.xxx" для Макса). */
    private String messageId;

    private String text;

    private MsgUser from;

    /** Автор пересланного сообщения, если сообщение было переслано. */
    private MsgUser forwardedFrom;

    /** URL аудиовложения (голосового сообщения), если есть. */
    private String audioUrl;

    /** Распознанный текст голосового сообщения (Макс распознаёт автоматически). */
    private String audioTranscription;

    public boolean hasText() {
        return text != null && !text.trim().isEmpty();
    }

    public boolean isVoice() {
        return audioUrl != null;
    }
}