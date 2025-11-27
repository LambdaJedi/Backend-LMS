package com.happytraining.happytraining.controller;

import com.happytraining.happytraining.model.User;
import com.happytraining.happytraining.service.UserService;
import com.happytraining.happytraining.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) throws ExecutionException, InterruptedException {
        User u = userService.getUserById(id);
        return u == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(u);
    }

    @GetMapping("/me")
    public ResponseEntity<User> me() throws ExecutionException, InterruptedException {
        String uid = AuthUtil.currentUid();
        if (uid == null) return ResponseEntity.status(401).build();
        User u = userService.getUserById(uid);
        return u == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(u);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) throws ExecutionException, InterruptedException {
        User created = userService.createUser(user);
        return ResponseEntity.status(201).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updates) throws ExecutionException, InterruptedException {
        User updated = userService.updateUser(id, updates);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) throws ExecutionException, InterruptedException {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}


