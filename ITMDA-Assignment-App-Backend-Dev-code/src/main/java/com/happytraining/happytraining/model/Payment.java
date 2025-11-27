package com.happytraining.happytraining.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Payment {

    @DocumentId
    private String id;

    private String userId;
    private String courseId;
    private double amount;
    private String currency;
    private String status;
    private String description;

    // Use Date instead of LocalDateTime for Firestore compatibility
    private Date timestamp;

    // Default constructor
    public Payment() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Handle timestamp conversion
    @PropertyName("timestamp")
    public Date getTimestamp() { return timestamp; }

    @PropertyName("timestamp")
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    // Helper method to set LocalDateTime (convert to Date for Firestore)
    public void setLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            this.timestamp = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    // Helper method to get LocalDateTime
    public LocalDateTime getLocalDateTime() {
        if (timestamp != null) {
            return LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
        }
        return null;
    }
}


