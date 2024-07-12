package com.example.testTg_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class TestTgBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestTgBotApplication.class, args);
	}
}
