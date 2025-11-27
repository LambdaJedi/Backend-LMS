package com.happytraining.happytraining.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Course {
    @DocumentId
    private String id;
    private String title;
    private String description;
    private String instructor;
    private String category;
    private String mode;
    private String startDate;
    private String endDate;
    private String createdAt;
    private String status;
    private List<String> enrolledUsers;

    // 🆕 ADD THESE FOR CALENDAR INTEGRATION
    private String startTime;     // "10:00 AM"
    private String endTime;       // "11:30 AM"
    private String schedule;      // "Mon,Wed,Fri" or "Daily"
    private String location;      // "Room 101", "Online"
    private String color;         // Calendar event color

    public Course() {
        this.createdAt = String.valueOf(new Date());
        this.enrolledUsers = new ArrayList<>();
        this.color = "#5E35B1"; // Default purple
    }
}

