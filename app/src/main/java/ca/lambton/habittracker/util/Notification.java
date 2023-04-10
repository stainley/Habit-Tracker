package ca.lambton.habittracker.util;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String message;

    private LocalDateTime startNotification;
    private LocalDateTime endNotification;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getStartNotification() {
        return startNotification;
    }

    public void setStartNotification(LocalDateTime startNotification) {
        this.startNotification = startNotification;
    }

    public LocalDateTime getEndNotification() {
        return endNotification;
    }

    public void setEndNotification(LocalDateTime endNotification) {
        this.endNotification = endNotification;
    }
}
