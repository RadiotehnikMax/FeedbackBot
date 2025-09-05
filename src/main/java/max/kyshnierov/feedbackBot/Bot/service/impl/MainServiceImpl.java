package max.kyshnierov.feedbackBot.Bot.service.impl;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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

    private List<String> roles = List.of(
            "Механік",
            "Електрик",
            "Менеджер"
        );
    private List<String> workstation = List.of(
            "Філія на Хмельницькій",
            "Філія на Київській",
            "Філія на Хрещатику",
            "Філія на Віденській",
            "Філія на Володимира Великого",
            "Філія на Шевченка"
        );

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
        
        if (roles.contains(message) || workstation.contains(message)) { 
            return true; 
        }
        return false;
    }

    private void roleHandler(String message) {
        switch (message) {
            case "Механік":
            case "Електрик":
            case "Менеджер":
                if (repository.findByChatId(chatId) == null) {
                    repository.save(Users.builder()
                        .chatId(chatId)
                        .role((message.equals("Механік") ? "Механік" : message.equals("Електрик") ? "Електрик" : "Менеджер"))
                        .workstation("") // тимчасово
                        .build());
                }
                bot.sendMessageWithKeyboard(chatId, "Виберіть вашу філію:", keyboards.getWorkStationMenu());
                break;
            case "Філія на Хмельницькій":
            case "Філія на Київській":
            case "Філія на Хрещатику":
            case "Філія на Віденській":
            case "Філія на Володимира Великого":
            case "Філія на Шевченка":
                if (repository.findByChatId(chatId) != null) {
                    Users user = repository.findByChatId(chatId);
                    user.setWorkstation((message.equals("Філія на Хмельницькій") ? "Філія на Хмельницькій" :
                                            message.equals("Філія на Київській") ? "Філія на Київській" :
                                            message.equals("Філія на Хрещатику") ? "Філія на Хрещатику" :
                                            message.equals("Філія на Віденській") ? "Філія на Віденській" :
                                            message.equals("Філія на Володимира Великого") ? "Філія на Володимира Великого" : "Філія на Шевченка"
                                            ));
                    repository.save(user);
                }
                bot.sendMessage(chatId, "Дякуємо! Ви успішно зареєстровані.");
                bot.sendMessage(chatId, "Тепер ви можете надсилати свої скарги, побажання або пропозиції анонімно.");
                break;
            default:
                bot.sendMessage(chatId, "Невідома команда. Спробуйте /help для списку доступних команд.");
                break;
        }
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
