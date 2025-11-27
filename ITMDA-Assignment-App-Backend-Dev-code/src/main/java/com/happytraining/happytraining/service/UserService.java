package com.happytraining.happytraining.service;

import com.happytraining.happytraining.model.User;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface UserService {
    List<User> getAllUsers() throws ExecutionException, InterruptedException;
    User getUserById(String id) throws ExecutionException, InterruptedException;
    User createUser(User user) throws ExecutionException, InterruptedException;
    User updateUser(String id, User updates) throws ExecutionException, InterruptedException;
    void deleteUser(String id) throws ExecutionException, InterruptedException;
}
