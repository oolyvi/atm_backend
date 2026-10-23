package com.bank.atm_monolithic.notification;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationRoutingService routingService;

    public NotificationController(NotificationRoutingService routingService) {
        this.routingService = routingService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@Valid @RequestBody Notification request) {
        /*
        http://localhost:8081/notification/send

        {
            "type": "EMAIL",
            "recipient": "recipient_mail@gmail.com",
            "title": "Title",
            "message": "Perfect message!"
        }
         */
        routingService.routeNotification(request);
        return ResponseEntity.ok("[ATM_DEBUG]: Notification was sent!");
    }
}