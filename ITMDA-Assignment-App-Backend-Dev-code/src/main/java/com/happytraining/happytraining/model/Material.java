package com.happytraining.happytraining.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Material {
    private String id;
    private String courseId;
    private String title;
    private String description;
    private String fileUrl;

    // ==================== TEMPORARY SUBMISSION FIELDS ====================
    private String type; // "LECTURE", "ASSIGNMENT", "SUBMISSION"
    private String learnerId;
    private String learnerName;
    private String status; // "PENDING", "REVIEWED"
    private String comment;
    private String submittedAt;
    private String reviewedAt;
    private String reviewerId;

    // Helper method to check if this is a submission
    public boolean isSubmission() {
        return "SUBMISSION".equals(this.type);
    }
}

