package max.kyshnierov.feedbackBot.Bot.controler;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.extern.log4j.Log4j;
import max.kyshnierov.feedbackBot.Bot.service.MainService;

@SuppressWarnings("deprecation")
@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.username}")
    String botUsername;
    @Value("${bot.token}")
    String botToken;

    private final MainService mainService;

    public TelegramBot(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            log.info("Received message: " + update.getMessage().getText());
            mainService.setChatId(update.getMessage().getChatId());
            mainService.processMessage(update.getMessage().getText());
        }
    }

    private void sendAnswer(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (Exception e) {
                log.error("Error occurred while sending message: " + e.getMessage());
            }
        }
    }

    public void sendMessage(Long chatId, String text) {
        if (chatId != null) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(text);
            sendAnswer(message);
        }
    }

    public void sendMessageWithKeyboard(Long chatId, String text, org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(keyboard);
        sendAnswer(message);
    }

    public void sendFile(Long chatId, String filePath) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(new InputFile(new File(filePath)));

        try {
            execute(sendDocument);
            log.info("File sent successfully");
        } catch (TelegramApiException e) {
            log.error("Failed to send file: " + e.getMessage());
        }
    }
}
