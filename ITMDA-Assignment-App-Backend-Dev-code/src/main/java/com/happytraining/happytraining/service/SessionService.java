package com.happytraining.happytraining.service;

import com.happytraining.happytraining.model.Session;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SessionService {
    Session startSession(Session s) throws ExecutionException, InterruptedException;
    Session endSession(String sessionId) throws ExecutionException, InterruptedException;
    List<Session> getActiveSessions() throws ExecutionException, InterruptedException;
}
