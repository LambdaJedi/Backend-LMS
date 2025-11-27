package com.happytraining.happytraining.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;
    private String name;
    private String email;
    private String role; // "user" or "admin"
    private String profileImageUrl;

    public String getUid() {
        return "";
    }

    public void setUid(String id) {
    }
}

