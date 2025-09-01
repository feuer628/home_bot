package com.cheremnov.bot.schedule;

import com.cheremnov.bot.command.door.TuyaAdapter;
import com.cheremnov.bot.messages.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DoorStatusSchedule {

    /**
     * �������� � ������� �������� �� ����� ����������� ������� ��������� (� �������������)
     */
    private static final Integer INTERVAL = 6 * 60 * 60 * 1000; // 6 �����

    private static Long lastSendTime;

    @Autowired
    private MessageSender messageSender;

    @Scheduled(fixedRate = 1000 * 60)
    public void performRegularTask() {
        try {
            Map<String, Object> statusInfo = TuyaAdapter.getStatus();
            if (!(boolean) statusInfo.get("online")) {
                if (lastSendTime == null || (System.currentTimeMillis() - INTERVAL) > lastSendTime) {
                    messageSender.sendAllTrustedUsers("������� �������...");
                    lastSendTime = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            messageSender.sendAllTrustedUsers("������ ��������� ������� ��������: " + e.getMessage());
            throw e;
        }
    }
}
