package com.cheremnov.bot.db;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * Миграция существующих H2-данных на строковые ключи платформ.
 * Старые числовые идентификаторы принадлежали Telegram-боту, поэтому
 * к ним добавляется префикс "tg:". Запускается после schema update Hibernate
 * и до инициализации кеша доверенных пользователей (см. UserChecker).
 */
@Slf4j
@Component
public class DataMigration {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void migrate() {
        // 1) Приводим колонки к строковому типу (Hibernate мог не изменить тип)
        alter("alter table trusted_user alter column id set data type varchar(255)");
        alter("alter table subscriber alter column id set data type varchar(255)");
        alter("alter table subscriber alter column chat_id set data type varchar(255)");
        alter("alter table group_tour_subscriber_chat_ids alter column subscriber_chat_ids set data type varchar(255)");

        // 2) Дополняем старые значения префиксом платформы Telegram
        prefix("update trusted_user set id = 'tg:' || id where id not like '%:%'");
        prefix("update subscriber set id = 'tg:' || id where id not like '%:%'");
        prefix("update subscriber set chat_id = 'tg:' || chat_id where chat_id is not null and chat_id not like '%:%'");
        prefix("update group_tour_subscriber_chat_ids set subscriber_chat_ids = 'tg:' || subscriber_chat_ids where subscriber_chat_ids is not null and subscriber_chat_ids not like '%:%'");

        // 3) Переносим подписки групп из старой ARRAY-колонки group_tour.subscriber_chat_ids
        migrateGroupTourSubscriptions();
    }

    private void migrateGroupTourSubscriptions() {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("select id, subscriber_chat_ids from group_tour");
            for (Map<String, Object> row : rows) {
                Object groupId = getIgnoreCase(row, "ID");
                Object value = getIgnoreCase(row, "SUBSCRIBER_CHAT_IDS");
                if (groupId == null || value == null) {
                    continue;
                }
                Integer existing = jdbcTemplate.queryForObject(
                        "select count(*) from group_tour_subscriber_chat_ids where group_tour_id = ?",
                        Integer.class, groupId);
                if (existing != null && existing > 0) {
                    continue;
                }
                for (String chatKey : toArray(value)) {
                    if (chatKey == null || chatKey.isBlank()) {
                        continue;
                    }
                    String key = chatKey.contains(":") ? chatKey : "tg:" + chatKey;
                    jdbcTemplate.update(
                            "insert into group_tour_subscriber_chat_ids (group_tour_id, subscriber_chat_ids) values (?, ?)",
                            groupId, key);
                }
                log.info("Миграция: перенесены подписки группы {}", groupId);
            }
        } catch (Exception e) {
            log.debug("Миграция подписок групп пропущена: {}", e.getMessage());
        }
    }

    private List<String> toArray(Object value) {
        if (value instanceof Object[] array) {
            return java.util.Arrays.stream(array).map(String::valueOf).toList();
        }
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            java.util.ArrayList<String> result = new java.util.ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                result.add(String.valueOf(Array.get(value, i)));
            }
            return result;
        }
        if (value instanceof java.sql.Array sqlArray) {
            try {
                return toArray(sqlArray.getArray());
            } catch (Exception e) {
                return List.of();
            }
        }
        return List.of(String.valueOf(value));
    }

    private Object getIgnoreCase(Map<String, Object> row, String column) {
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(column)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void alter(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            log.debug("Миграция (alter, пропущено): {} — {}", sql, e.getMessage());
        }
    }

    private void prefix(String sql) {
        try {
            int updated = jdbcTemplate.update(sql);
            if (updated > 0) {
                log.info("Миграция: обновлено строк {}", updated);
            }
        } catch (Exception e) {
            log.warn("Миграция (update, пропущено): {} — {}", sql, e.getMessage());
        }
    }
}