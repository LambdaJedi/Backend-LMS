package com.happytraining.happytraining.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.happytraining.happytraining.model.User;
import com.happytraining.happytraining.service.UserService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserServiceImpl implements UserService {

    private final CollectionReference users;

    public UserServiceImpl(Firestore firestore) {
        this.users = firestore.collection("users");
    }

    @Override
    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = users.get();
        return future.get().toObjects(User.class);
    }

    @Override
    public User getUserById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snap = users.document(id).get().get();
        return snap.exists() ? snap.toObject(User.class) : null;
    }

    @Override
    public User createUser(User user) throws ExecutionException, InterruptedException {
        String id = user.getUid();
        DocumentReference ref = (id == null || id.isEmpty()) ? users.document() : users.document(id);
        ref.set(user).get();
        user.setUid(ref.getId());
        return user;
    }

    @Override
    public User updateUser(String id, User updates) throws ExecutionException, InterruptedException {
        DocumentReference ref = users.document(id);
        ref.set(updates, SetOptions.merge()).get();
        return getUserById(id);
    }

    @Override
    public void deleteUser(String id) throws ExecutionException, InterruptedException {
        users.document(id).delete().get();
    }
}

