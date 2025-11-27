package com.happytraining.happytraining.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;

    public String getConversationId() {
        return "";
    }

    public Object getParticipants() {
        return null;
    }

    public void setConversationId(String id) {
    }

    public void setCreatedAt(Date date) {
    }

    public @org.jetbrains.annotations.NotNull Object getFrom() {
        return null;
    }

    public Object getTo() {
        return null;
    }
}
