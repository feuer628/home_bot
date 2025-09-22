package com.cheremnov.bot.schedule;

import com.cheremnov.bot.command.door.TuyaAdapter;
import com.cheremnov.bot.messages.MessageSender;
import com.cheremnov.bot.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DoorStatusSchedule {

    /**
     * Интервал, в течении которого не будет отправляться повторные сообщения об отключении домофона
     */
    private static final Integer INTERVAL = 6 * 60 * 60 * 1000; // 6 часов

    private static Long lastSendTime;

    private boolean isOffline = false;

    @Autowired
    private MessageSender messageSender;

    @Scheduled(fixedRate = 1000 * 60)
    public void performRegularTask() {
        try {
            Map<String, Object> deviceInfo = TuyaAdapter.getDeviceInfo();
            if ((boolean) deviceInfo.get("success")) {
                Map<String, Object> statusInfo = (Map<String, Object>) TuyaAdapter.getDeviceInfo().get("result");
                if ((boolean) statusInfo.get("online") && Boolean.TRUE == getStatusInfo(statusInfo, "ipc_ch1")) {
                    if (isOffline) {
                        isOffline = false;
                        messageSender.sendAllTrustedUsers("Домофон онлайн!");
                    }
                } else {
                    isOffline = true;
                    if (lastSendTime == null || (System.currentTimeMillis() - INTERVAL) > lastSendTime) {
                        messageSender.sendAllTrustedUsers("Домофон оффлайн...");
                        lastSendTime = System.currentTimeMillis();
                    }
                }
            } else {
                messageSender.sendAllTrustedUsers("Ответ на запрос статуса домофона пришел с ошибкой: \n" + JsonUtils.objectToString(deviceInfo));
            }
        } catch (Exception e) {
            messageSender.sendAllTrustedUsers("Ошибка при получении статуса домофона: " + e.getMessage());
            throw e;
        }
    }

    private Object getStatusInfo(Map<String, Object> statusInfo, String name) {
        for (Map<String, Object> map : (List<Map<String, Object>>) statusInfo.get("status")) {
            if (name.equals(map.get("code"))) {
                return map.get("value");
            }
        }
        return null;
    }
}
