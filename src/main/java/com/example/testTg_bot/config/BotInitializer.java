package com.example.testTg_bot.config;

import jakarta.annotation.Resource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BotInitializer {

    @Resource
    TelegramBot bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
//        log.info("Bot initializing");
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot((LongPollingBot) bot);
            log.info("Bot initialized");
        } catch (TelegramApiException e) {
            log.error("Error initializing: " + e.getMessage());
        }
    }
}
