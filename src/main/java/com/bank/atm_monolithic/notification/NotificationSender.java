package com.bank.atm_monolithic.notification;

public interface NotificationSender {
    NotificationType getType();
    void send(Notification request) throws Exception;
}