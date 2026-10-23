package com.bank.atm_monolithic.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Notification {
    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotBlank(message = "Recipient cannot be empty")
    private String recipient;

    private String title;

    @NotBlank(message = "Message context cannot be empty")
    private String message;

    public Notification(NotificationType type, String recipient, String title, String message) {
        this.type = type;
        this.recipient = recipient;
        this.title = title;
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}