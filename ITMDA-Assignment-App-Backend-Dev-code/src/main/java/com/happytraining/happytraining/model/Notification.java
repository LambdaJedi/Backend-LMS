package com.happytraining.happytraining.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private String id;
    private String userId;
    private String message;
    private boolean read;
    private LocalDateTime timestamp;

    public void setCreatedAt(Date date) {
    }
}

