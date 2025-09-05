package max.kyshnierov.feedbackBot.Bot.service;

public interface MainService {
    void processMessage(String message);
    void setChatId(Long chatId);
}
