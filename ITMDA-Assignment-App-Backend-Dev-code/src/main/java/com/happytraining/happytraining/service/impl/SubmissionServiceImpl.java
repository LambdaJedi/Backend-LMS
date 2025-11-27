package com.happytraining.happytraining.service.impl;

import com.happytraining.happytraining.model.Submission;
import com.happytraining.happytraining.service.SubmissionService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final Firestore db = FirestoreClient.getFirestore();
    private static final String COLLECTION_NAME = "submissions";

    @Override
    public Submission updateSubmission(String id, Submission updates) throws ExecutionException, InterruptedException {
        Map<String, Object> updateData = new HashMap<>();

        if (updates.getStatus() != null) updateData.put("status", updates.getStatus());
        if (updates.getComment() != null) updateData.put("comment", updates.getComment());
        if (updates.getReviewerComment() != null) updateData.put("reviewerComment", updates.getReviewerComment());

        // If status is being updated to "Reviewed", set reviewedAt timestamp
        if ("Reviewed".equals(updates.getStatus())) {
            updateData.put("reviewedAt", String.valueOf(new Date()));
        }

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(id)
                .update(updateData);
        future.get();

        return getSubmissionById(id);
    }

    // Keep all your existing methods from previous implementation
    @Override
    public List<Submission> getAllSubmissions() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .orderBy("date", Query.Direction.DESCENDING)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Submission> submissions = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            submissions.add(document.toObject(Submission.class));
        }
        return submissions;
    }

    @Override
    public Submission getSubmissionById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        return document.exists() ? document.toObject(Submission.class) : null;
    }

    @Override
    public Submission createSubmission(Submission submission) throws ExecutionException, InterruptedException {
        String id = db.collection(COLLECTION_NAME).document().getId();
        submission.setId(id);

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(id)
                .set(submission);
        future.get();

        return submission;
    }

    @Override
    public List<Submission> getSubmissionsByLearner(String learnerId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("learnerId", learnerId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Submission> submissions = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            submissions.add(document.toObject(Submission.class));
        }
        return submissions;
    }

    @Override
    public List<Submission> getSubmissionsByCourse(String courseId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("courseId", courseId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Submission> submissions = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            submissions.add(document.toObject(Submission.class));
        }
        return submissions;
    }

    @Override
    public void deleteSubmission(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }
}
