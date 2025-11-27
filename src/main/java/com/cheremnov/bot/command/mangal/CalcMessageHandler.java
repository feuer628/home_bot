package com.cheremnov.bot.command.mangal;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.text.DecimalFormat;

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


            // считаем стоимость металла: площадь * 2600  рублей за квадрат
            double sumMetall = square(l, d, h) * 2600;
            // считаем длину реза четверки: общая длинна реза * 90 рублей
            double sumReza = lRez4(l, d, h) * 90;
            // считаем длину реза двойки: общая длинна реза * 90 рублей
            double sumReza2 = lRez2(l) * 60;
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

            // рассчитываем коэффициент мангала:
            // за единицу берем 75см длина и 45см ширина,
            // мангал меньше этого все равно будет рассчитан по единице
            // далее пропорционально увеличиваем стоимость
            double koefL = Math.max(l / 800, 1);
            double koefD = Math.max(d / 450, 1);

            double itogo = sumMetall + sumReza + sumReza2 + sumDvig + (sumSureshki + svarshik + mne) * koefL * koefD;
            // Округляем до ближайшей сотни
            long roundedNumber = Math.round(itogo / 100) * 100;
            // Форматируем строку с использованием DecimalFormat
            DecimalFormat df = new DecimalFormat("#,##0");
            String msg = message.getText() + "\nСумма: " + df.format(roundedNumber);
            bot.sendText(message.getChatId(), "Сумма мангала с размерами " + String.join("; ", split[0], split[1], split[2]) + ":\n" + df.format(roundedNumber),
                    getBean(MangalOrderCallbackHandler.class).getSingleButton("Заказать мангал", msg));

        } catch (NumberFormatException e) {
            bot.sendText(message.getChatId(), "Неверный формат размеров");
            return false;
        }
        return true;
    }

    private double lRez4(double l, double d, double h) {
        int k = (int) (l / 64.1);
        return ((l + h * 2 + l * 2.05) + // передняя стенка
                (d * 2 + h * 2.5) * 2 + // боковые стенки
                (l * 2 + d * 2) + // дно
                (l * 2 + h * 2 + 89 * k + 40 * 4 + 48 * 3) +//задняя стенка
                (l * 4.1) +// задняя стенка с отверстиями
                (l * 3.5) +// верхняя пластина над звездочками
                (l * 6) + // опоры под сетку
                (k * 163 * 4)) / 1000; //кругляшки около звездочек (считаем по более крупной звезде)
    }

    private double lRez2(double l) {
        // в длине реза 2 мм считаем только звездочки (2 штуки на привод и несколько на сам мангал)
        double lZvezdi = 328; //мм
        int count = (int) (2 + l / 64.1);
        return lZvezdi * count / 1000;
    }

    private double square(double l, double d, double h) {
        return (l + d) * (l + d) / 1000000;
//        double v = (l * h + // передняя стенка
//                d * h * 2 + // две боковые стенки
//                l * d + // дно
//                l * (h + 60 + 60 + h + 25) // задняя стенка со всеми прилегающими деталями
//        ); // мм^2
//        return v / 1000000;// площадь возвращаем в квадратных метрах
    }
}
