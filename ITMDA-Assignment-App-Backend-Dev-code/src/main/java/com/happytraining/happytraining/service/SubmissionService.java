package com.happytraining.happytraining.service;

import com.happytraining.happytraining.model.Submission;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SubmissionService {
    List<Submission> getAllSubmissions() throws ExecutionException, InterruptedException;
    Submission getSubmissionById(String id) throws ExecutionException, InterruptedException;
    Submission createSubmission(Submission submission) throws ExecutionException, InterruptedException;
    Submission updateSubmission(String id, Submission updates) throws ExecutionException, InterruptedException;
    void deleteSubmission(String id) throws ExecutionException, InterruptedException;
    List<Submission> getSubmissionsByLearner(String learnerId) throws ExecutionException, InterruptedException;
    List<Submission> getSubmissionsByCourse(String courseId) throws ExecutionException, InterruptedException;
}
