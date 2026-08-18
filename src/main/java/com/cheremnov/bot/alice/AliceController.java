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
        if (!validateToken(token)) {
            log.warn("❌ Неавторизованная попытка открытия калитки. IP: {}, Token: {}",
                    getRequestIp(), token != null ? "***" + token.substring(token.length() - 4) : "отсутствует");
            return ResponseEntity.status(401).body("{\"error\":\"Unauthorized\"}");
        }

        log.info("✅ Запрос на открытие калитки с колонки: {}", request.getEntityId());
        if (TuyaAdapter.openDoor()) {
            aliceService.sendToSpecificAlice(request.getEntityId(), "Калитка открылась");
        } else {
            aliceService.sendToSpecificAlice(request.getEntityId(), "Возникли сложности, калитка не открылась");
        }
        return ResponseEntity.ok("{\"status\":\"ok\"}");
    }

    @PostMapping("/open-gate")
    public ResponseEntity<String> openGate(@RequestBody OpenDoorRequest request,
                                           @RequestHeader(value = "X-API-Token", required = false) String token) {
        if (!validateToken(token)) {
            log.warn("❌ Неавторизованная попытка открытия ворот. IP: {}, Token: {}",
                    getRequestIp(), token != null ? "***" + token.substring(token.length() - 4) : "отсутствует");
            return ResponseEntity.status(401).body("{\"error\":\"Unauthorized\"}");
        }

        log.info("✅ Запрос на открытие ворот с колонки: {}", request.getEntityId());
        if (TuyaAdapter.openGate()) {
            aliceService.sendToSpecificAlice(request.getEntityId(), "Ворота открылись");
        } else {
            aliceService.sendToSpecificAlice(request.getEntityId(), "Возникли сложности, ворота не открылись");
        }
        return ResponseEntity.ok("{\"status\":\"ok\"}");
    }

    @PostMapping("/close-gate")
    public ResponseEntity<String> closeGate(@RequestBody OpenDoorRequest request,
                                            @RequestHeader(value = "X-API-Token", required = false) String token) {
        if (!validateToken(token)) {
            log.warn("❌ Неавторизованная попытка закрытия ворот. IP: {}, Token: {}",
                    getRequestIp(), token != null ? "***" + token.substring(token.length() - 4) : "отсутствует");
            return ResponseEntity.status(401).body("{\"error\":\"Unauthorized\"}");
        }

        log.info("✅ Запрос на закрытие ворот с колонки: {}", request.getEntityId());
        if (TuyaAdapter.closeGate()) {
            aliceService.sendToSpecificAlice(request.getEntityId(), "Ворота закрылись");
        } else {
            aliceService.sendToSpecificAlice(request.getEntityId(), "Возникли сложности, ворота не закрылись");
        }
        return ResponseEntity.ok("{\"status\":\"ok\"}");
    }

    @PostMapping("/stop-gate")
    public ResponseEntity<String> stopGate(@RequestBody OpenDoorRequest request,
                                           @RequestHeader(value = "X-API-Token", required = false) String token) {
        if (!validateToken(token)) {
            log.warn("❌ Неавторизованная попытка остановки ворот. IP: {}, Token: {}",
                    getRequestIp(), token != null ? "***" + token.substring(token.length() - 4) : "отсутствует");
            return ResponseEntity.status(401).body("{\"error\":\"Unauthorized\"}");
        }

        log.info("✅ Запрос на остановку ворот с колонки: {}", request.getEntityId());
        if (TuyaAdapter.stopGate()) {
            aliceService.sendToSpecificAlice(request.getEntityId(), "Ворота остановлены");
        } else {
            aliceService.sendToSpecificAlice(request.getEntityId(), "Возникли сложности, ворота не остановились");
        }
        return ResponseEntity.ok("{\"status\":\"ok\"}");
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