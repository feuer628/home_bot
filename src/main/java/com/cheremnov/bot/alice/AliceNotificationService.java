package com.cheremnov.bot.alice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AliceNotificationService {

    // Секреты из automation
    private static final String BROADCAST_WEBHOOK = "alice_broadcast_secret";
    private static final String SPECIFIC_WEBHOOK = "alice_specific_secret";
    private static final String PLAY_AUDIO_WEBHOOK = "alice_play_audio_secret";
    private final RestTemplate restTemplate = new RestTemplate();
    // URL вашего HA снаружи (или localhost если HA и Java на одном сервере)
    @Value("${ha.webhook.url:http://192.168.1.75:8123/}")
    private String haBaseUrl;

    /**
     * Отправить сообщение на ВСЕ колонки Алисы
     */
    public void sendToAllAlices(String message) {
        String url = haBaseUrl + "/api/webhook/alice_broadcast_secret";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);  // КРИТИЧЕСКИ ВАЖНО!

            HttpEntity<MessageRequest> entity = new HttpEntity<>(
                    new MessageRequest(message),
                    headers
            );

            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    entity,
                    String.class
            );

            log.info("Отправлено: " + message + ", ответ HA: " + response.getStatusCode());

        } catch (Exception e) {
            log.error("Ошибка: ", e);
        }
    }

    /**
     * Отправить сообщение на КОНКРЕТНУЮ колонку
     * @param entityId entity_id колонки, например "media_player.yandex_station_kitchen"
     * @param message  текст для озвучки
     */
    public void sendToSpecificAlice(String entityId, String message) {
        String url = haBaseUrl + "/api/webhook/" + SPECIFIC_WEBHOOK;

        try {
            restTemplate.postForEntity(
                    url,
                    new SpecificMessageRequest(entityId, message),
                    String.class
            );
            log.info("Отправлено на {}: {}", entityId, message);
        } catch (Exception e) {
            log.error("Ошибка отправки TTS: ", e);
        }
    }

    /**
     * Воспроизвести аудиофайл (например, голосовое из Макса) на колонке.
     * В Home Assistant должна быть автоматизация с вебхуком alice_play_audio_secret,
     * вызывающая media_player.play_media с URL из trigger.json.url.
     *
     * @param entityId entity_id колонки
     * @param audioUrl прямая ссылка на аудио
     */
    public void playAudio(String entityId, String audioUrl) {
        String url = haBaseUrl + "/api/webhook/" + PLAY_AUDIO_WEBHOOK;
        try {
            restTemplate.postForEntity(
                    url,
                    new AudioPlayRequest(entityId, audioUrl),
                    String.class);
            log.info("Воспроизведение на {}: {}", entityId, audioUrl);
        } catch (Exception e) {
            log.error("Ошибка воспроизведения аудио: ", e);
        }
    }

    // DTO для запросов
    public record MessageRequest(String message) {
    }

    public record SpecificMessageRequest(String entity_id, String message) {
    }

    public record AudioPlayRequest(String entity_id, String url) {
    }
}
