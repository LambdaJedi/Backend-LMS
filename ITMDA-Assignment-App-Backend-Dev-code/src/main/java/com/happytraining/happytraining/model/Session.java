package com.happytraining.happytraining.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private String id;
    private String userId;
    private String courseId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean active;

    public void setStartedBy(String uid) {
    }

    public void setStartedAt(Date date) {
    }
}

