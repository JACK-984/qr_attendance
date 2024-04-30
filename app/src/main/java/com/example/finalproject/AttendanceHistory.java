package com.example.finalproject;

import java.util.Date;

public class AttendanceHistory {

    private String courseId;
    private Date sessionDateTime;
    private String status;

    public AttendanceHistory(){};
    // Constructor
    public AttendanceHistory(String courseId, Date sessionDateTime, String status) {
        this.courseId = courseId;
        this.sessionDateTime = sessionDateTime;
        this.status = status;
    }

    // Getters and setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Date getSessionDateTime() {
        return sessionDateTime;
    }

    public void setSessionDateTime(Date sessionDateTime) {
        this.sessionDateTime = sessionDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
