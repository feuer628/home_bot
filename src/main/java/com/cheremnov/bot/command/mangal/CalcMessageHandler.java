package com.cheremnov.bot.command.mangal;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CalcMessageHandler extends AbstractMessageHandler {

    @Override
    public boolean handleMessage(Message message, Bot bot) {
        String[] split = message.getText().split(";");
        if (split.length != 3) {
            bot.sendText(message.getChatId(), "Неверный формат размеров");
            return false;
        }
        try {
            double l = Double.parseDouble(split[0]);
            double d = Double.parseDouble(split[1]);
            double h = Double.parseDouble(split[2]);


            // считаем стоимость металла: объем * 7850 * 100 рублей
            double sumMetall = objom() * 7850 * 100;

            // считаем длину реза четверки: общая длинна реза * 90 рублей
            double sumReza = lRez4() * 90;

            // считаем длину реза двойки: общая длинна реза * 90 рублей
            double sumReza2 = lRez2() * 60;

            // стоимость корпуса двигателя
            double sumDvig = 1400;
            // шурешки:
            // 1500 двигатель
            // 200 заклепки, Выключатели
            // 250 кабель питания
            // 700 цепь и замки
            // 400 кованные элементы
            // 500 краска
            double sumSureshki = 3500;
            // работа сварщика
            double svarshik = 5000;
            // мне
            double mne = 5000;
            double itogo = sumMetall + sumReza + sumReza2 + sumDvig + sumSureshki + svarshik + mne;
            bot.sendText(message.getChatId(), "Сумма мангала с размерами " + String.join("; ", split[0], split[1], split[2]) + " :\n" + itogo,
                    getBean(MangalOrderCallbackHandler.class).getSingleButton("Заказать мангал", message.getText()));

        } catch (NumberFormatException e) {
            bot.sendText(message.getChatId(), "Неверный формат размеров");
            return false;
        }
        return true;
    }

    private double lRez4() {
        // TODO
        return 0;
    }

    private double lRez2() {
        // TODO
        return 0;
    }

    private double objom() {
        // TODO
        return 0;
    }
}
