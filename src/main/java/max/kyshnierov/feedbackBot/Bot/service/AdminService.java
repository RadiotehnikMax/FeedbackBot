package max.kyshnierov.feedbackBot.Bot.service;

import java.io.IOException;

public interface AdminService {
    void newMessage();
    void getFeedback();
    void exportCSV(String filePath, Long chatId) throws IOException;
}
