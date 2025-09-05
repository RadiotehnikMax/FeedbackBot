package max.kyshnierov.feedbackBot.Bot.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component
public class Keyboards {
    public ReplyKeyboardMarkup getRoleMenu() {
        return roleMenu();
    }  
    public ReplyKeyboardMarkup getWorkStationMenu() {
        return workStationMenu();
    }

    private ReplyKeyboardMarkup roleMenu() {
        ReplyKeyboardMarkup roleKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Механік");
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Електрик");
        keyboard.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Менеджер");
        keyboard.add(row3);

        roleKeyboardMarkup.setKeyboard(keyboard);
        roleKeyboardMarkup.setResizeKeyboard(true);
        roleKeyboardMarkup.setOneTimeKeyboard(false);

        return roleKeyboardMarkup;
    }
    private ReplyKeyboardMarkup workStationMenu() {
        ReplyKeyboardMarkup workStationKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Філія на Хмельницькій");
        row1.add("Філія на Київській");
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Філія на Хрещатику");
        row2.add("Філія на Віденській");
        keyboard.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Філія на Володимира Великого");
        row3.add("Філія на Шевченка");
        keyboard.add(row3);

        workStationKeyboardMarkup.setKeyboard(keyboard);
        workStationKeyboardMarkup.setResizeKeyboard(true);
        workStationKeyboardMarkup.setOneTimeKeyboard(false);

        return workStationKeyboardMarkup;
    }
}
