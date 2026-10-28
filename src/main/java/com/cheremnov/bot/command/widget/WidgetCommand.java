package com.cheremnov.bot.command.widget;

import com.cheremnov.bot.command.AbstractCommandHandler;
import com.cheremnov.bot.command.door.TuyaAdapter;
import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.messenger.MsgUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Служебные команды удалённого управления (например, с Android-виджета).
 * Виджет перезаписывает текст слота командой "/widget &lt;действие&gt;",
 * команда выполняется здесь, результат пишется только в лог.
 *
 * Доступна только вызовам от ботов из белого списка (см. bot.widget-bots):
 * обычным пользователям отвечает отказом.
 */
@Slf4j
@Component
public class WidgetCommand extends AbstractCommandHandler {

    private static final String ACTION_GATE_OPEN = "gate_open";
    private static final String ACTION_GATE_STOP = "gate_stop";
    private static final String ACTION_GATE_CLOSE = "gate_close";
    private static final String ACTION_DOOR_OPEN = "door_open";

    @Override
    public String getCommandName() {
        return "widget";
    }

    @Override
    public String getCommandDescription() {
        return "Служебные команды виджета";
    }

    @Override
    public boolean isCommandHidden() {
        return true;
    }

    @Override
    public boolean isPublicCommand() {
        // авторизация выполняется внутри handleCommand (только отправитель-бот)
        return true;
    }

    @Override
    public void handleCommand(MsgMessage message, Messenger messenger) {
        MsgUser from = message.getFrom();
        if (from == null || !from.isBot()) {
            messenger.sendText(message.getChatId(), "Служебная команда, недоступна из чата");
            return;
        }
        String action = message.getText().substring(getCommandName().length() + 1).trim();
        try {
            boolean success;
            String description;
            switch (action) {
                case ACTION_GATE_OPEN -> {
                    success = TuyaAdapter.openGate();
                    description = "Ворота открыты";
                }
                case ACTION_GATE_STOP -> {
                    success = TuyaAdapter.stopGate();
                    description = "Ворота остановлены";
                }
                case ACTION_GATE_CLOSE -> {
                    success = TuyaAdapter.closeGate();
                    description = "Ворота закрыты";
                }
                case ACTION_DOOR_OPEN -> {
                    success = TuyaAdapter.openDoor();
                    description = "Калитка открыта";
                }
                default -> {
                    log.warn("Неизвестная команда виджета: {}", action);
                    return;
                }
            }
            // результат — только в лог, в чат ничего не отправляем
            if (success) {
                log.info("Виджет: {} -> {}", action, description);
            } else {
                log.warn("Виджет: {} -> не выполнено", action);
            }
        } catch (Exception e) {
            log.error("Виджет: {} -> ошибка: {}", action, e.getMessage());
        }
    }
}