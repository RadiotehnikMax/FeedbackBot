package max.kyshnierov.feedbackBot.Bot.service.impl;

import org.springframework.stereotype.Service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;

import max.kyshnierov.feedbackBot.Bot.DataBase.dao.FeedbackRepository;
import max.kyshnierov.feedbackBot.Bot.DataBase.dao.UserRepository;
import max.kyshnierov.feedbackBot.Bot.DataBase.entity.Feedback;
import max.kyshnierov.feedbackBot.Bot.DataBase.entity.Users;
import max.kyshnierov.feedbackBot.Bot.controler.TelegramBot;

import lombok.extern.log4j.Log4j;
import max.kyshnierov.feedbackBot.Bot.service.AdminService;
import max.kyshnierov.feedbackBot.Bot.service.MainService;
import max.kyshnierov.feedbackBot.Bot.util.Keyboards;
import max.kyshnierov.feedbackBot.Bot.util.Role;
import max.kyshnierov.feedbackBot.Bot.util.Workstation;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    @Value("${bot.admin}")
    private Long adminId;

    @Autowired
    @Lazy
    private TelegramBot bot;
    @Autowired
    private Keyboards keyboards;

    @Autowired
    private UserRepository repository;
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private AdminService admin;

    private Long chatId;

    @Override
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public void processMessage(String message) {
        if (message != null && message.startsWith("/")) {
            String command = message.split(" ")[0]; // отримати команду до першого пробілу
            switch (command) {
                case "/start":
                    bot.sendMessageWithKeyboard(chatId, "Вітаємо! Це бот для анонімних скарг/побажань/пропозицій.\n" + 
                                "Виберіть свою посаду:",
                                keyboards.getRoleMenu()
                                );
                    break;
                case "/help":
                    bot.sendMessage(chatId, "Доступні команди:\n" +
                                "/start - почати\n" + 
                                "/help - допомога"
                                );
                    break;
                case "/list":
                    if (adminId != null && adminId.equals(chatId)) {
                        admin.getFeedback();
                    }
                    break;
                case "/csv":
                    if (adminId != null && adminId.equals(chatId)) {
                        bot.sendMessage(chatId, "Експорт CSV...");
                        try{
                            admin.exportCSV("feedback.csv", chatId);
                        } catch (IOException e) {
                            bot.sendMessage(chatId, "При експорті файлу сталась помилка!\nВибачте за незручності");
                            log.error("Export CSV error: " + e.getMessage());
                        }  
                    }
                    break;
                default:
                    log.warn("invalid command: " + command);
                    bot.sendMessage(chatId, "Невідома команда. Спробуйте /help для списку доступних команд.");
                    break;
            }
        } else if (message != null && isRoleMessage(message)) {
            roleHandler(message);
        } 
        if (message != null && isRoleMessage(message) == false) {
            Users user = repository.findByChatId(chatId);
            if (user != null && chatId.equals(user.getChatId())) {
                feedbackHandler(message);
            }
        }
    }

    private boolean isRoleMessage(String message) {

        if (Role.isEquals(message) || Workstation.isEquals(message)) {
            return true;
        }
        return false;
    }

    private void roleHandler(String message) {
        // Спроба визначити роль через enum Role
        try {
            Role role = Role.fromDisplayName(message);
            if (repository.findByChatId(chatId) == null) {
                repository.save(Users.builder()
                    .chatId(chatId)
                    .role(role.getDisplayName()) // записуємо displayName
                    .workstation("") // тимчасово
                    .build());
            }
            bot.sendMessageWithKeyboard(chatId, "Виберіть вашу філію:", keyboards.getWorkStationMenu());
            return;
        } catch (IllegalArgumentException ignored) {
            // message не є роллю, пробуємо як філію
        }

        // Спроба визначити філію через enum Workstation
        try {
            Workstation ws = Workstation.fromDisplayName(message);
            Users user = repository.findByChatId(chatId);
            if (user != null) {
                user.setWorkstation(ws.getDisplayName()); // записуємо displayName
                repository.save(user);
                bot.sendMessage(chatId, "Дякуємо! Ви успішно зареєстровані.");
                bot.sendMessage(chatId, "Тепер ви можете надсилати свої скарги, побажання або пропозиції анонімно.");
            } else {
                bot.sendMessage(chatId, "Спочатку оберіть роль.");
            }
            return;
        } catch (IllegalArgumentException ignored) {
            // message не є філією
        }

        // Якщо не роль і не філія
        bot.sendMessage(chatId, "Невідома команда. Спробуйте /help для списку доступних команд.");
    }

    private void feedbackHandler(String message) {
        if (message != null) {
            Users user = repository.findByChatId(chatId);
            feedbackRepository.save(Feedback.builder()
                                .text(message)
                                .isList(false)
                                .user(user)
                                .build()
            );
            admin.newMessage();
        }
        bot.sendMessage(chatId, "Дякуємо за ваш відгук! Він був успішно отриманий.");
    }
}
