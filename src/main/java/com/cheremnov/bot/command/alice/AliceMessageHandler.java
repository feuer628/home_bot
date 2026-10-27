package com.cheremnov.bot.command.alice;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.alice.Alice;
import com.cheremnov.bot.alice.AliceNotificationService;
import com.cheremnov.bot.command.AbstractMessageHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AliceMessageHandler extends AbstractMessageHandler {

    @Setter
    private String aliceId;

    @Autowired
    private AliceNotificationService aliceNotificationService;

    /**
     * Режим работы с голосовыми сообщениями:
     * original — воспроизводить оригинальную запись на колонке (media_player.play_media);
     * tts      — озвучивать распознанный текст голосом Алисы (работает на любых колонках).
     */
    @Value("${alice.voice-mode:original}")
    private String voiceMode;

    @Override
    public boolean handleMessage(MsgMessage message, Messenger messenger) {
        String msg = message.getText();
        if (message.isVoice()) {
            return handleVoice(message, messenger);
        }
        if (msg == null || msg.isBlank()) {
            messenger.sendText(message.getChatId(), "Пока можно отправлять только текст или голосовое сообщение...");
            return false;
        }
        say(msg);
        return true;
    }

    /**
     * Голосовое сообщение: оригинал — на колонку, распознанный Максом текст — в чат.
     * В режиме tts колонка озвучивает распознанный текст.
     */
    private boolean handleVoice(MsgMessage message, Messenger messenger) {
        String transcription = message.getAudioTranscription();
        boolean hasTranscription = transcription != null && !transcription.isBlank();
        log.debug("Голосовое в потоке say_alice: режим={}, url={}, транскрипция: {}", voiceMode,
                message.getAudioUrl(), hasTranscription ? transcription : "<нет>");

        if ("tts".equalsIgnoreCase(voiceMode)) {
            if (!hasTranscription) {
                messenger.sendText(message.getChatId(), "Голосовое получено, но распознать текст не удалось");
                return true;
            }
            say(transcription);
            messenger.sendText(message.getChatId(), "Голосовое озвучено на Алисе.\nРаспознано: " + transcription);
            return true;
        }

        play(message.getAudioUrl());
        String text = hasTranscription
                ? "Голосовое отправлено на Алису.\nРаспознано: " + transcription
                : "Голосовое отправлено на Алису (распознать текст не удалось)";
        messenger.sendText(message.getChatId(), text);
        return true;
    }

    private void say(String text) {
        if ("all".equals(aliceId)) {
            aliceNotificationService.sendToAllAlices(text);
        } else {
            aliceNotificationService.sendToSpecificAlice(Alice.valueOf(aliceId).getId(), text);
        }
    }

    private void play(String audioUrl) {
        if ("all".equals(aliceId)) {
            for (Alice alice : Alice.values()) {
                aliceNotificationService.playAudio(alice.getId(), audioUrl);
            }
        } else {
            aliceNotificationService.playAudio(Alice.valueOf(aliceId).getId(), audioUrl);
        }
    }
}