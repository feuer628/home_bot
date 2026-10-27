package com.cheremnov.bot.max;

import com.cheremnov.bot.messenger.KeyButton;
import com.cheremnov.bot.messenger.Keyboard;
import com.cheremnov.bot.messenger.Keys;
import com.cheremnov.bot.messenger.MenuCommand;
import com.cheremnov.bot.max.dto.MaxMessage;
import com.cheremnov.bot.max.dto.MaxSendMessageResult;
import com.cheremnov.bot.max.dto.MaxUpdateList;
import com.cheremnov.bot.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Низкоуровневый REST-клиент API Макса (https://platform-api2.max.ru).
 * Авторизация — заголовок "Authorization: &lt;access_token&gt;".
 */
@Slf4j
public class MaxApi {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final String baseUrl;
    private final String token;
    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public MaxApi(String baseUrl, String token, OkHttpClient client) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.token = token;
        this.client = client;
    }

    /**
     * Информация о боте (GET /me). Используется для проверки токена.
     */
    public JsonNode getMe() {
        return execute("GET", "/me", null, null);
    }

    /**
     * Отправка сообщения. Ключ чата "mx:u&lt;id&gt;" — пользователю, "mx:c&lt;id&gt;" — в чат/канал.
     */
    public MaxSendMessageResult sendMessage(String chatKey, String text, Keyboard keyboard) {
        Map<String, String> query = new LinkedHashMap<>();
        if (Keys.isMaxUserChat(chatKey)) {
            query.put("user_id", String.valueOf(Keys.maxRawId(chatKey)));
        } else {
            query.put("chat_id", String.valueOf(Keys.maxRawId(chatKey)));
        }
        JsonNode result = execute("POST", "/messages", query, newMessageBody(text, keyboard, true));
        return convert(result, MaxSendMessageResult.class);
    }

    /**
     * Редактирование текста сообщения. keyboard == null — убрать клавиатуру.
     */
    public void editMessageText(String messageId, String text, Keyboard keyboard) {
        execute("PUT", "/messages", Map.of("message_id", messageId), newMessageBody(text, keyboard, true));
    }

    /**
     * Замена только клавиатуры сообщения. keyboard == null — убрать клавиатуру.
     */
    public void setInlineMarkup(String messageId, Keyboard keyboard) {
        execute("PUT", "/messages", Map.of("message_id", messageId), newMessageBody(null, keyboard, false));
    }

    /**
     * Ответ на callback-нажатие.
     * API требует либо message, либо notification — при отсутствии текста
     * отправляется пустое уведомление (просто подтверждение нажатия).
     */
    public void answerCallback(String callbackId, String notification) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("notification", notification == null ? "" : notification);
        execute("POST", "/answers", Map.of("callback_id", callbackId), body);
    }

    /**
     * Установка команд меню бота.
     */
    public void setCommands(List<MenuCommand> commands) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MenuCommand command : commands) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", command.getName());
            item.put("description", command.getDescription());
            list.add(item);
        }
        execute("PATCH", "/me/commands", null, Map.of("commands", list));
    }

    /**
     * Long polling: получение обновлений (GET /updates).
     *
     * @param marker указатель на следующее обновление из предыдущего ответа (null — получить последнее)
     * @param timeoutSec тайм-аут долгого опроса, сек
     */
    public MaxUpdateList getUpdates(Long marker, int timeoutSec, List<String> types) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("timeout", String.valueOf(timeoutSec));
        query.put("limit", "100");
        if (marker != null) {
            query.put("marker", String.valueOf(marker));
        }
        if (types != null && !types.isEmpty()) {
            query.put("types", String.join(",", types));
        }
        JsonNode result = execute("GET", "/updates", query, null);
        return convert(result, MaxUpdateList.class);
    }

    /**
     * Получение сообщения по идентификатору (GET /messages/{messageId}).
     * Используется для дозапроса транскрипции голосового сообщения.
     */
    public MaxMessage getMessage(String messageId) {
        JsonNode result = execute("GET", "/messages/" + urlEncode(messageId), null, null);
        JsonNode messages = result.get("messages");
        JsonNode node = (messages != null && messages.isArray() && messages.size() > 0) ? messages.get(0) : result;
        return convert(node, MaxMessage.class);
    }

    // ------------------------------------------------------------------

    private Map<String, Object> newMessageBody(String text, Keyboard keyboard, boolean includeText) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (includeText) {
            body.put("text", text);
        }
        if (includeText || keyboard != null) {
            // Пустой массив вложений убирает клавиатуру, непустой — заменяет её
            List<Map<String, Object>> attachments = new ArrayList<>();
            if (keyboard != null && keyboard.getRows() != null && !keyboard.getRows().isEmpty()) {
                attachments.add(keyboardAttachment(keyboard));
            }
            body.put("attachments", attachments);
        }
        return body;
    }

    private Map<String, Object> keyboardAttachment(Keyboard keyboard) {
        List<List<Map<String, Object>>> buttons = new ArrayList<>();
        for (List<KeyButton> row : keyboard.getRows()) {
            List<Map<String, Object>> outRow = new ArrayList<>();
            for (KeyButton button : row) {
                Map<String, Object> json = new LinkedHashMap<>();
                json.put("text", button.getText());
                if (button.isLink()) {
                    json.put("type", "link");
                    json.put("url", button.getUrl());
                } else {
                    json.put("type", "callback");
                    json.put("payload", button.getCallbackData() == null ? "" : button.getCallbackData());
                }
                outRow.add(json);
            }
            buttons.add(outRow);
        }
        Map<String, Object> attachment = new LinkedHashMap<>();
        attachment.put("type", "inline_keyboard");
        attachment.put("payload", Map.of("buttons", buttons));
        return attachment;
    }

    private JsonNode execute(String method, String path, Map<String, String> query, Object body) {
        String url = buildUrl(path, query);
        for (int attempt = 0; ; attempt++) {
            try {
                Request.Builder builder = new Request.Builder()
                        .url(url)
                        .header("Authorization", token);
                String jsonBody = body == null ? null : JsonUtils.objectToString(body);
                if ("GET".equals(method)) {
                    builder.get();
                } else {
                    builder.method(method, jsonBody == null
                            ? RequestBody.create(JSON, "")
                            : RequestBody.create(JSON, jsonBody));
                }
                Response response = client.newCall(builder.build()).execute();
                String content = response.body() == null ? "" : response.body().string();
                if (response.code() == 429 && attempt == 0) {
                    // Превышен лимит запросов — одна повторная попытка
                    log.warn("API Макса: превышен лимит запросов (429), повтор через 1.5 сек");
                    Thread.sleep(1500);
                    continue;
                }
                if (!response.isSuccessful()) {
                    throw new MaxApiException(response.code(), "API Макса " + method + " " + path
                            + " -> HTTP " + response.code() + ": " + content);
                }
                return content.isBlank() ? mapper.createObjectNode() : mapper.readTree(content);
            } catch (MaxApiException e) {
                throw e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new MaxApiException("Запрос к API Макса прерван", e);
            } catch (IOException | RuntimeException e) {
                throw new MaxApiException("Ошибка запроса к API Макса " + method + " " + path, e);
            }
        }
    }

    private <T> T convert(JsonNode node, Class<T> type) {
        try {
            return mapper.treeToValue(node, type);
        } catch (JsonProcessingException e) {
            throw new MaxApiException("Ошибка разбора ответа API Макса", e);
        }
    }

    private String buildUrl(String path, Map<String, String> query) {
        StringBuilder sb = new StringBuilder(baseUrl).append(path);
        if (query != null && !query.isEmpty()) {
            sb.append('?');
            boolean first = true;
            for (Map.Entry<String, String> entry : query.entrySet()) {
                if (!first) {
                    sb.append('&');
                }
                first = false;
                sb.append(entry.getKey()).append('=').append(urlEncode(entry.getValue()));
            }
        }
        return sb.toString();
    }

    private String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}