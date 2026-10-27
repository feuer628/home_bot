package com.cheremnov.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.cheremnov.bot"})
@Slf4j
@EnableScheduling
public class StartBot {


    public static void main(String[] args) {
        try {
            new SpringApplicationBuilder(StartBot.class)
                    .bannerMode(Banner.Mode.OFF)
                    .run(args);
        } catch (Exception e) {
            log.error("Ошибка при инициализации бота", e);
        }
    }

}