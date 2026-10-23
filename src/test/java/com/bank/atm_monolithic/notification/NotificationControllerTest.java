package com.bank.atm_monolithic.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationRoutingService routingService;

    @Test
    void testSendNotificationSuccess() throws Exception {
        Notification notification = new Notification(NotificationType.EMAIL, "test@example.com", "Test Title", "Test Message");

        doNothing().when(routingService).routeNotification(any(Notification.class));

        mockMvc.perform(post("/notification/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andExpect(content().string("[ATM_DEBUG]: Notification dispatched processing queue."));
    }

    @Test
    void testSendNotificationValidationFailure() throws Exception {
        Notification notification = new Notification(NotificationType.EMAIL, "", "Test Title", "");

        mockMvc.perform(post("/notification/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isInternalServerError());
    }
}