package max.kyshnierov.feedbackBot.Bot.util;

public enum Role {
    MECHANIC("Механік"),
    ELECTRICIAN("Електрик"),
    MANAGER("Менеджер");

    private final String name;
    private final String displayName;

    Role(String name) {
        this.name = name;
        this.displayName = name;
    }

    public String getRole() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static boolean isEquals(String value) {
        for (Role role : Role.values()) {
            if (role.name.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static Role fromDisplayName(String value) {
        for (Role role : Role.values()) {
            if (role.name.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No Role with display name " + value);
    }
}