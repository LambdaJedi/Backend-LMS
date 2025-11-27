package com.happytraining.happytraining.dto;

import java.util.List;

public class EnrollmentResponse {
    private String message;
    private String courseId;
    private List<String> enrolledUsers;

    public EnrollmentResponse() {}

    public EnrollmentResponse(String message, String courseId, List<String> enrolledUsers) {
        this.message = message;
        this.courseId = courseId;
        this.enrolledUsers = enrolledUsers;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public List<String> getEnrolledUsers() {
        return enrolledUsers;
    }

    public void setEnrolledUsers(List<String> enrolledUsers) {
        this.enrolledUsers = enrolledUsers;
    }
}

