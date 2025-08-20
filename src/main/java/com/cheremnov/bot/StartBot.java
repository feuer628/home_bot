package com.cheremnov.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication(scanBasePackages = {"com.cheremnov.bot"})
@Slf4j
public class StartBot {


    public static void main(String[] args) {
        try {
            ApplicationContext context = new SpringApplicationBuilder(StartBot.class)
                    .bannerMode(Banner.Mode.OFF)
                    .run(args);

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            Bot bot = context.getBean(Bot.class);
            telegramBotsApi.registerBot(bot);
        } catch (Exception e) {
            log.error("Ошибка при инициализации бота", e);
            throw new RuntimeException(e);
        }
    }

}
