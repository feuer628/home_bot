package com.cheremnov.bot.alice;

import com.cheremnov.bot.command.door.TuyaAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AliceController {

    @Autowired
    private AliceNotificationService aliceService;

    @PostMapping("/open-door")
    public ResponseEntity<String> openDoor(@RequestBody OpenDoorRequest request) {
        log.info("Поступил запрос на открытие калитки с колонки: {}", request.getEntityId());

        if (TuyaAdapter.openDoor()) {
            aliceService.sendToSpecificAlice(request.getEntityId(), "Калитка открылась");
        } else {
            // Отправляем ошибку на ту же колонку, а не всем
            aliceService.sendToSpecificAlice(request.getEntityId(),
                    "Возникли сложности, калитка не открылась");
        }
        return ResponseEntity.ok("{\"status\":\"ok\"}");
    }

    // DTO для запроса
    @Data
    public static class OpenDoorRequest {
        private String entityId;  // entity_id колонки
        // можно добавить другие поля
    }
}
