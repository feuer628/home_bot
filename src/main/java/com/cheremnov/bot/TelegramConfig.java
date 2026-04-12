package com.cheremnov.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
public class TelegramConfig {

    @Bean
    public DefaultBotOptions telegramBotOptions(
            @Value("${telegram.proxy.enabled}") boolean proxyEnabled,
            @Value("${telegram.proxy.host}") String proxyHost,
            @Value("${telegram.proxy.port}") int proxyPort,
            @Value("${telegram.proxy.user}") String proxyUser,
            @Value("${telegram.proxy.password}") String proxyPassword) {

        DefaultBotOptions options = new DefaultBotOptions();
        if (proxyEnabled) {
            System.setProperty("java.net.socks.username", proxyUser);
            System.setProperty("java.net.socks.password", proxyPassword);

            options.setProxyHost(proxyHost);
            options.setProxyPort(proxyPort);
            options.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
        }

        return options;
    }
}
