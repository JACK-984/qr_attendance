package com.example.finalproject;

import java.util.Date;

public class Attendance {
    private String studentId;
    private Date sessionDateTime;
    private String status;

    public Attendance() {
        // Required empty public constructor for Firestore
    }

    public Attendance(String studentId, Date sessionDateTime, String status) {
        this.studentId = studentId;
        this.sessionDateTime = sessionDateTime;
        this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }
    public String getStatus(){return status;}
    public void setStatus(String status){this.status = status;}
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

