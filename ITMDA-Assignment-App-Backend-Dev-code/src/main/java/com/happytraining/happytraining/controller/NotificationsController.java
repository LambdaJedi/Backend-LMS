package com.happytraining.happytraining.controller;

import com.happytraining.happytraining.model.Notification;
import com.happytraining.happytraining.service.NotificationService;
import com.happytraining.happytraining.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/notifications")
public class NotificationsController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable String userId) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<Notification> createNotification(@RequestBody Notification n) throws ExecutionException, InterruptedException {
        Notification created = notificationService.createNotification(n);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) throws ExecutionException, InterruptedException {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
