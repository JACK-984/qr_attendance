package com.example.finalproject;

public class Course {
    private String courseName;
    private String qrImageUrl;

    public Course(String courseName, String qrImageUrl) {
        this.courseName = courseName;
        this.qrImageUrl = qrImageUrl;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getQrImageUrl() {
        return qrImageUrl;
    }
}

