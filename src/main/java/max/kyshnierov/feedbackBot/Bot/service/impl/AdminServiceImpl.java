package max.kyshnierov.feedbackBot.Bot.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;

import jakarta.transaction.Transactional;
import max.kyshnierov.feedbackBot.Bot.DataBase.dao.FeedbackRepository;
import max.kyshnierov.feedbackBot.Bot.DataBase.entity.Feedback;
import max.kyshnierov.feedbackBot.Bot.DataBase.entity.Users;
import max.kyshnierov.feedbackBot.Bot.controler.TelegramBot;
import max.kyshnierov.feedbackBot.Bot.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {
    @Value("${bot.admin}")
    private Long adminId;

    @Autowired
    @Lazy
    private TelegramBot bot;
    @Autowired
    private FeedbackRepository repository;

    @Override
    public void newMessage() {
        bot.sendMessage(adminId, "Отримано новий відгук.\nНатисніть /list щоб подивить");
    }

    @Override
    @Transactional
    public void getFeedback() {
        List<Feedback> feedbacks = repository.findByIsListFalse();

        for (Feedback feedback : feedbacks) {
            Users user = feedback.getUser();
            String message = String.format("Відгук: %s\nПрацівник: %s\nРобоче місце: %s",
                feedback.getText(),
                user.getRole(),
                user.getWorkstation());
            bot.sendMessage(adminId, message);
            feedback.setList(true);
            repository.save(feedback);
        }
        bot.sendMessage(adminId, "Щоб експортувати csv, введіть /csv");
    }

    @Override
    public void exportCSV(String filePath, Long chatId) throws IOException {
        List<Feedback> feedbacks = repository.findTop100ByOrderByIdDesc();
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            String[] header = {"id", "text", "isList", "role", "workstation"};
            writer.writeNext(header);

            for (Feedback feedback : feedbacks) {
                String role = feedback.getUser() != null ? feedback.getUser().getRole() : "";
                String workstation = feedback.getUser() != null ? feedback.getUser().getWorkstation() : "";

                String[] data = {
                    String.valueOf(feedback.getId()),
                    feedback.getText(),
                    String.valueOf(feedback.isList()),
                    role,
                    workstation
                };
                writer.writeNext(data);
            }
        }
        bot.sendFile(chatId, filePath);
    }
}
