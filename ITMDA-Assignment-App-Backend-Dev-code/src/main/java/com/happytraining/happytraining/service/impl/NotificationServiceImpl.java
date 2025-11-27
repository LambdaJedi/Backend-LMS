package com.happytraining.happytraining.service.impl;

import com.google.cloud.firestore.*;
import com.happytraining.happytraining.model.Notification;
import com.happytraining.happytraining.service.NotificationService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final CollectionReference notifications;

    public NotificationServiceImpl(Firestore firestore) {
        this.notifications = firestore.collection("notifications");
    }

    @Override
    public List<Notification> getNotificationsForUser(String userId) throws ExecutionException, InterruptedException {
        Query q = notifications.whereEqualTo("userId", userId).orderBy("createdAt", Query.Direction.DESCENDING);
        return q.get().get().toObjects(Notification.class);
    }

    @Override
    public Notification createNotification(Notification n) throws ExecutionException, InterruptedException {
        DocumentReference ref = notifications.document();
        n.setId(ref.getId());
        n.setCreatedAt(new java.util.Date());
        ref.set(n).get();
        return n;
    }

    @Override
    public void markAsRead(String notificationId) throws ExecutionException, InterruptedException {
        notifications.document(notificationId).update("read", true).get();
    }
}
