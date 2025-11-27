package com.happytraining.happytraining.service.impl;

import com.google.cloud.firestore.*;
import com.happytraining.happytraining.model.Session;
import com.happytraining.happytraining.service.SessionService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class SessionServiceImpl implements SessionService {

    private final CollectionReference sessions;

    public SessionServiceImpl(Firestore firestore) {
        this.sessions = firestore.collection("sessions");
    }

    @Override
    public Session startSession(Session s) throws ExecutionException, InterruptedException {
        DocumentReference ref = sessions.document();
        s.setId(ref.getId());
        s.setStartedAt(new java.util.Date());
        s.setActive(true);
        ref.set(s).get();
        return s;
    }

    @Override
    public Session endSession(String sessionId) throws ExecutionException, InterruptedException {
        DocumentReference ref = sessions.document(sessionId);
        ref.update("active", false, "endedAt", new java.util.Date()).get();
        return ref.get().get().toObject(Session.class);
    }

    @Override
    public List<Session> getActiveSessions() throws ExecutionException, InterruptedException {
        Query q = sessions.whereEqualTo("active", true);
        return q.get().get().toObjects(Session.class);
    }
}

