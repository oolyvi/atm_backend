package com.bank.atm_monolithic.notification;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketNotificationSender implements NotificationSender {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationSender(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public NotificationType getType() {
        return NotificationType.WEB_SOCKET;
    }

    @Override
    public void send(Notification request) {
        messagingTemplate.convertAndSend("/topic/user/" + request.getRecipient(), request);
    }
}