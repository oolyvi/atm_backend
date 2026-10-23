package com.bank.atm_monolithic.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationRoutingServiceTest {

    private NotificationSender emailSenderMock;
    private NotificationRoutingService routingService;

    @BeforeEach
    void setUp() {
        emailSenderMock = mock(NotificationSender.class);
        when(emailSenderMock.getType()).thenReturn(NotificationType.EMAIL);

        routingService = new NotificationRoutingService(List.of(emailSenderMock));
    }

    @Test
    void testRouteNotificationSuccess() throws Exception {
        Notification notification = new Notification(NotificationType.EMAIL, "test@example.com", "Title", "Message");

        routingService.routeNotification(notification);

        verify(emailSenderMock, times(1)).send(notification);
    }

    @Test
    void testRouteNotificationNoChannelFound() {
        Notification notification = new Notification(NotificationType.SMS, "123456789", "Title", "Message");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routingService.routeNotification(notification);
        });

        assertTrue(exception.getMessage().contains("No channel found for"));
    }
}