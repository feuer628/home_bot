package com.cheremnov.bot.alice;

import com.cheremnov.bot.command.door.TuyaAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.function.BooleanSupplier;

@RestController
@Slf4j
public class AliceController {

    @Autowired
    private AliceNotificationService aliceService;

    // Получаем токен из аргументов командной строки (--token=...)
    @Value("${token}")
    private String telegramToken;

    // Метод проверки токена
    private boolean validateToken(String requestToken) {
        return requestToken != null && !requestToken.isEmpty() && telegramToken.equals(requestToken);
    }

    @PostMapping("/open-door")
    public ResponseEntity<String> openDoor(@RequestBody OpenDoorRequest request,
                                           @RequestHeader(value = "X-API-Token", required = false) String token) {
        return handle(request, token, "open-door", "открытие калитки",
                TuyaAdapter::openDoor, "Калитка открылась", "Возникли сложности, калитка не открылась");
    }

    @PostMapping("/open-gate")
    public ResponseEntity<String> openGate(@RequestBody OpenDoorRequest request,
                                           @RequestHeader(value = "X-API-Token", required = false) String token) {
        return handle(request, token, "open-gate", "открытие ворот",
                TuyaAdapter::openGate, "Ворота открылись", "Возникли сложности, ворота не открылись");
    }

    @PostMapping("/close-gate")
    public ResponseEntity<String> closeGate(@RequestBody OpenDoorRequest request,
                                            @RequestHeader(value = "X-API-Token", required = false) String token) {
        return handle(request, token, "close-gate", "закрытие ворот",
                TuyaAdapter::closeGate, "Ворота закрылись", "Возникли сложности, ворота не закрылись");
    }

    @PostMapping("/stop-gate")
    public ResponseEntity<String> stopGate(@RequestBody OpenDoorRequest request,
                                           @RequestHeader(value = "X-API-Token", required = false) String token) {
        return handle(request, token, "stop-gate", "остановка ворот",
                TuyaAdapter::stopGate, "Ворота остановлены", "Возникли сложности, ворота не остановились");
    }

    private ResponseEntity<String> handle(OpenDoorRequest request,
                                          String token,
                                          String action,
                                          String actionRu,
                                          BooleanSupplier supplier,
                                          String okMessage,
                                          String failMessage) {
        if (!validateToken(token)) {
            log.warn("❌ Неавторизованная попытка {}. IP: {}, Token: {}",
                    actionRu, getRequestIp(),
                    token != null ? "***" + token.substring(token.length() - 4) : "отсутствует");
            return ResponseEntity.status(401)
                    .body("{\"status\":\"error\",\"action\":\"" + action + "\",\"message\":\"Unauthorized\"}");
        }

        log.info("✅ Запрос на {} с колонки: {}", actionRu, request.getEntityId());
        try {
            boolean success = supplier.getAsBoolean();
            sendToAlice(request.getEntityId(), success ? okMessage : failMessage);
            if (success) {
                return ResponseEntity.ok("{\"status\":\"ok\",\"action\":\"" + action + "\"}");
            }
            return ResponseEntity.status(502)
                    .body("{\"status\":\"error\",\"action\":\"" + action + "\",\"message\":\"" + failMessage + "\"}");
        } catch (Exception e) {
            log.error("💥 Ошибка при {}: {}", actionRu, e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body("{\"status\":\"error\",\"action\":\"" + action + "\",\"message\":\"Internal error\"}");
        }
    }

    // Отправка сообщения на Алису, если entityId не равен "widget"
    private void sendToAlice(String entityId, String message) {
        if (!"widget".equals(entityId)) {
            aliceService.sendToSpecificAlice(entityId, message);
        }
    }

    // Вспомогательный метод для логирования IP
    private String getRequestIp() {
        try {
            var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }

    @Data
    public static class OpenDoorRequest {
        private String entityId;
    }
}