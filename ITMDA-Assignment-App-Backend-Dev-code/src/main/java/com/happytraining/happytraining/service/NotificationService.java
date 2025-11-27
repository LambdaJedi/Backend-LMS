package com.happytraining.happytraining.service;

import com.happytraining.happytraining.model.Notification;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface NotificationService {
    List<Notification> getNotificationsForUser(String userId) throws ExecutionException, InterruptedException;
    Notification createNotification(Notification n) throws ExecutionException, InterruptedException;
    void markAsRead(String notificationId) throws ExecutionException, InterruptedException;
}

