package com.happytraining.happytraining.controller;

import com.happytraining.happytraining.model.Submission;
import com.happytraining.happytraining.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/submissions")
public class SubmissionsController {

    @Autowired
    private SubmissionService submissionService;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    // File upload endpoint
    @PostMapping("/upload")
    public ResponseEntity<?> uploadAssessment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("learnerId") String learnerId,
            @RequestParam("learnerName") String learnerName,
            @RequestParam("courseId") String courseId,
            @RequestParam("courseName") String courseName,
            @RequestParam(value = "comment", required = false) String comment)
            throws ExecutionException, InterruptedException {

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Check file size (10MB limit)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File size exceeds 10MB limit");
            }

            // Check file type
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            if (!isAllowedFileType(fileExtension)) {
                return ResponseEntity.badRequest().body("File type not allowed. Only PDF, Word, PowerPoint files are allowed");
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            String filePath = uploadPath.resolve(uniqueFileName).toString();

            // Save file
            Files.copy(file.getInputStream(), Paths.get(filePath));

            // Create submission object
            Submission submission = new Submission();
            submission.setLearnerId(learnerId);
            submission.setLearnerName(learnerName);
            submission.setCourseId(courseId);
            submission.setCourseName(courseName);
            submission.setFileName(originalFileName);
            submission.setFileUrl("/api/submissions/files/" + uniqueFileName);
            submission.setFileSize(formatFileSize(file.getSize()));
            submission.setFileType(fileExtension.toUpperCase());
            submission.setComment(comment);
            submission.setStatus("Pending");

            Submission created = submissionService.createSubmission(submission);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }

    // Serve uploaded files
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                    .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update submission status with reviewer comments
    @PatchMapping("/{id}/review")
    public ResponseEntity<Submission> reviewSubmission(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam(required = false) String reviewerComment)
            throws ExecutionException, InterruptedException {

        Submission updates = new Submission();
        updates.setStatus(status);
        updates.setReviewerComment(reviewerComment);

        Submission updated = submissionService.updateSubmission(id, updates);
        return ResponseEntity.ok(updated);
    }

    // Helper methods
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isAllowedFileType(String extension) {
        return List.of("pdf", "doc", "docx", "ppt", "pptx").contains(extension);
    }

    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }

    // Your existing endpoints...
    @GetMapping
    public ResponseEntity<List<Submission>> getAllSubmissions() throws ExecutionException, InterruptedException {
        List<Submission> submissions = submissionService.getAllSubmissions();
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Submission> getSubmissionById(@PathVariable String id) throws ExecutionException, InterruptedException {
        Submission submission = submissionService.getSubmissionById(id);
        return submission != null ? ResponseEntity.ok(submission) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Submission> createSubmission(@RequestBody Submission submission) throws ExecutionException, InterruptedException {
        Submission created = submissionService.createSubmission(submission);
        return ResponseEntity.status(201).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Submission> updateSubmission(@PathVariable String id, @RequestBody Submission updates) throws ExecutionException, InterruptedException {
        Submission updated = submissionService.updateSubmission(id, updates);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable String id) throws ExecutionException, InterruptedException {
        submissionService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/learner/{learnerId}")
    public ResponseEntity<List<Submission>> getSubmissionsByLearner(@PathVariable String learnerId) throws ExecutionException, InterruptedException {
        List<Submission> submissions = submissionService.getSubmissionsByLearner(learnerId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Submission>> getSubmissionsByCourse(@PathVariable String courseId) throws ExecutionException, InterruptedException {
        List<Submission> submissions = submissionService.getSubmissionsByCourse(courseId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Submission>> getSubmissionsByStatus(@PathVariable String status) throws ExecutionException, InterruptedException {
        List<Submission> allSubmissions = submissionService.getAllSubmissions();
        List<Submission> filtered = allSubmissions.stream()
                .filter(sub -> status.equalsIgnoreCase(sub.getStatus()))
                .toList();
        return ResponseEntity.ok(filtered);
    }
}
