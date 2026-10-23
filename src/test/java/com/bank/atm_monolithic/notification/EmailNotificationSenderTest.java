package com.bank.atm_monolithic.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailNotificationSenderTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailNotificationSender emailNotificationSender;

    @Test
    void testGetType() {
        assertEquals(NotificationType.EMAIL, emailNotificationSender.getType());
    }

    @Test
    void testSendEmail() throws Exception {
        Notification notification = new Notification(NotificationType.EMAIL, "recipient@example.com", "Hello", "Welcome message");

        emailNotificationSender.send(notification);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assert sentMessage.getTo() != null;
        assertEquals("recipient@example.com", sentMessage.getTo()[0]);
        assertEquals("Hello", sentMessage.getSubject());
        assertEquals("Welcome message", sentMessage.getText());
    }
}