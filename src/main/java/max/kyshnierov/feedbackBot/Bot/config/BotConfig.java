package max.kyshnierov.feedbackBot.Bot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import max.kyshnierov.feedbackBot.Bot.controler.TelegramBot;

@Configuration
public class BotConfig {
    @Autowired
    private TelegramBot telegramBot;

    @Bean
    public TelegramBotsApi TelegramBotApi() throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(telegramBot);
        return api;
    }
}
