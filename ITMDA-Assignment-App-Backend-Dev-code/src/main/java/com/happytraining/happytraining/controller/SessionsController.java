package com.happytraining.happytraining.controller;

import com.happytraining.happytraining.model.Session;
import com.happytraining.happytraining.service.SessionService;
import com.happytraining.happytraining.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/sessions")
public class SessionsController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/start")
    public ResponseEntity<Session> startSession(@RequestBody Session s) throws ExecutionException, InterruptedException {
        String uid = AuthUtil.currentUid();
        if (uid == null) return ResponseEntity.status(401).build();
        s.setStartedBy(uid);
        Session started = sessionService.startSession(s);
        return ResponseEntity.status(201).body(started);
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<Session> endSession(@PathVariable String id) throws ExecutionException, InterruptedException {
        Session ended = sessionService.endSession(id);
        return ResponseEntity.ok(ended);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Session>> getActiveSessions() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(sessionService.getActiveSessions());
    }
}


