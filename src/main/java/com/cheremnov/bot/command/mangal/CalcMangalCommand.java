package com.cheremnov.bot.command.mangal;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CalcMangalCommand extends AbstractCommandHandler {


    @Override
    public String getCommandName() {
        return "calc";
    }

    @Override
    public String getCommandDescription() {
        return "Расчет стоимости мангала";
    }

    @Override
    public void handleCommand(Message message, Bot bot) {
        try {
            bot.execute(SendPhoto.builder()
                    .photo(new InputFile(CalcMangalCommand.class.getResourceAsStream("razmer.png"), "Мангал"))
                    .chatId(message.getChatId())
                    .caption("Пришлите размеры в следующем формате: L;D;H\nРазмеры указываются в миллиметрах, например, 700;450;176")
                    .build());
            bot.setMessageHandler(message.getChatId(), getBean(CalcMessageHandler.class));
        } catch (TelegramApiException e) {
            bot.sendAllTrustedUsers("Ошибка при ответе на расчет мангала:\n\n" + e.getMessage());
            bot.sendText(message.getChatId(), "Возникла ошибка, администратор уже уведомлен о ней");
        }
    }
}
