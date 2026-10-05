package ru.mirea.project.model;

public enum BookingStatus {
    ACCEPTED("Принят"),
    ASSEMBLING("В сборке"),
    ASSEMBLED("Собран"),
    PURCHASED("Выкуплен"),
    CANCELLED("Отменен");

    private final String title;

    BookingStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static BookingStatus fromTitle(String title) {
        for (BookingStatus s : values()) {
            if (s.title.equalsIgnoreCase(title) || s.name().equalsIgnoreCase(title)) {
                return s;
            }
        }
        return null;
    }
}