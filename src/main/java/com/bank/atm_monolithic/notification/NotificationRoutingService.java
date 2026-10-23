package com.bank.atm_monolithic.notification;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationRoutingService {

    private final Map<NotificationType, NotificationSender> senders = new HashMap<>();

    public NotificationRoutingService(List<NotificationSender> senderList) {
        for (NotificationSender sender : senderList) {
            senders.put(sender.getType(), sender);
        }
    }

    @Async
    public void routeNotification(Notification request) {
        NotificationSender sender = senders.get(request.getType());
        if (sender != null) {
            try {
                sender.send(request);
            } catch (Exception e) {
                System.err.println("Failed to route notification: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("No channel found for: " + request.getType());
        }
    }
}