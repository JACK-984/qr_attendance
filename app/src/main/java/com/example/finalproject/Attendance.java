package com.example.finalproject;

import java.util.Date;

public class Attendance {
    private String studentId;
    private Date sessionDateTime;

    public Attendance() {
        // Required empty public constructor for Firestore
    }

    public Attendance(String studentId, Date sessionDateTime) {
        this.studentId = studentId;
        this.sessionDateTime = sessionDateTime;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Date getSessionDateTime() {
        return sessionDateTime;
    }

    public void setSessionDateTime(Date sessionDateTime) {
        this.sessionDateTime = sessionDateTime;
    }
}

