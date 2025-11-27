package com.happytraining.happytraining.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import java.util.Date;

@Data
public class Submission {
    @DocumentId
    private String id;
    private String learnerId;      // Reference to User
    private String learnerName;    // Denormalized for easy display
    private String courseId;       // Reference to Course
    private String courseName;     // Denormalized for easy display
    private String fileUrl;
    private String fileName;
    private String fileSize;       // Add file size
    private String fileType;       // Add file type
    private String status;         // "Pending", "Reviewed"
    private String comment;
    private String date;           // Submission date (String like your Course model)
    private String reviewedAt;     // When it was reviewed
    private String reviewerComment; // Add reviewer comments

    public Submission() {
        this.status = "Pending";
        this.date = String.valueOf(new Date());
    }
}
