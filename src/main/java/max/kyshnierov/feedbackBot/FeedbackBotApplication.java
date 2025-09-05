package max.kyshnierov.feedbackBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class FeedbackBotApplication {
	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(FeedbackBotApplication.class, args);
	}
}
