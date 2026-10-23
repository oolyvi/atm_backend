package com.bank.atm_monolithic.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebSocketNotificationSenderTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketNotificationSender webSocketNotificationSender;

    @Test
    void testGetType() {
        assertEquals(NotificationType.WEB_SOCKET, webSocketNotificationSender.getType());
    }

    @Test
    void testSendWebSocketNotification() {
        Notification notification = new Notification(NotificationType.WEB_SOCKET, "user123", "Alert", "Balance low");

        webSocketNotificationSender.send(notification);

        verify(messagingTemplate).convertAndSend("/topic/user/user123", notification);
    }
}