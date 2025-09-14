package max.kyshnierov.feedbackBot.Bot.util;

public enum Workstation {
    BRANCH_KHMELNYTSKA("Філія на Хмельницькій"),
    BRANCH_KYIVSKA("Філія на Київській"),
    BRANCH_KHRESCHATYK("Філія на Хрещатику"),
    BRANCH_VIDENSKA("Філія на Віденській"),
    BRANCH_VOLODYMYRA_VELYKOHO("Філія на Володимира Великого"),
    BRANCH_SHEVCHENKA("Філія на Шевченка");

    private final String name;
    private final String displayName;

    Workstation(String name) {
        this.name = name;
        this.displayName = name;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static boolean isEquals(String value) {
        for (Workstation ws : Workstation.values()) {
            if (ws.name.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static Workstation fromDisplayName(String value) {
        for (Workstation ws : Workstation.values()) {
            if (ws.name.equals(value)) {
                return ws;
            }
        }
        throw new IllegalArgumentException("No Workstation with display name " + value);
    }
}
