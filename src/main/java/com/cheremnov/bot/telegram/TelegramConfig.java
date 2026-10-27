package com.cheremnov.bot.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
public class TelegramConfig {

    @Bean
    public DefaultBotOptions telegramBotOptions(
            @Value("${telegram.proxy.enabled:false}") boolean proxyEnabled,
            @Value("${telegram.proxy.host:}") String proxyHost,
            @Value("${telegram.proxy.port:}") String proxyPort,
            @Value("${telegram.proxy.user:}") String proxyUser,
            @Value("${telegram.proxy.password:}") String proxyPassword,
            @Value("${telegram.api.base-url:}") String apiBaseUrl,
            @Value("${telegram.api.secret:}") String apiSecret) {

        DefaultBotOptions options = new DefaultBotOptions();
        // прокси используется только при заданных host и port
        if (proxyEnabled && !proxyHost.isBlank() && !proxyPort.isBlank()) {
            System.setProperty("java.net.socks.username", proxyUser);
            System.setProperty("java.net.socks.password", proxyPassword);

            options.setProxyHost(proxyHost);
            options.setProxyPort(Integer.parseInt(proxyPort.trim()));
            options.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
        }

        // Релей через Cloudflare Worker.
        // Библиотека telegrambots строит итоговый URL как
        //   options.getBaseUrl() + token + "/" + method
        // при дефолтном baseUrl "https://api.telegram.org/bot".
        // Worker ждёт путь /<SECRET>/bot<TOKEN>/<method>,
        // поэтому baseUrl = <worker>/<SECRET>/bot
        if (apiBaseUrl != null && !apiBaseUrl.isBlank()) {
            String base = apiBaseUrl.replaceAll("/+$", "");
            if (apiSecret != null && !apiSecret.isBlank()) {
                base = base + "/" + apiSecret;
            }
            options.setBaseUrl(base + "/bot");
        }

        return options;
    }
}