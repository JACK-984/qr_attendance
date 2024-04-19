package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class QRScanner extends AppCompatActivity {
    private CompoundBarcodeView barcodeView;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        // Initialize the barcode scanner view
        barcodeView = findViewById(R.id.barcode_scanner);

        // Check if the camera permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, start scanning
            startScanning();
        }
    }

    // Method to start scanning when permission is granted
    private void startScanning() {
        // Set barcode callback to handle scanned results
        barcodeView.decodeContinuous(new BarcodeCallback() {
            // Inside the barcodeResult method of your QRScanner activity
            @Override
            public void barcodeResult(BarcodeResult result) {
                // Handle the scanned barcode result
                if (result.getText() != null) {
                    // Process the scanned QR code data (e.g., extract information)
                    String qrCodeID = result.getText();

                    // Query Firestore to retrieve course information based on qrCodeID
                    FirebaseFirestore.getInstance().collection("courses")
                            .whereEqualTo("qrCodeID", qrCodeID)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    // Retrieve course information
                                    String courseId = document.getId();
                                    String courseName = document.getString("name");
                                    // Check course schedule and mark attendance
                                    checkCourseSchedule(courseId, courseName);
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                Toast.makeText(QRScanner.this, "Failed to retrieve course information", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            // Method to check course schedule and mark attendance
            private void checkCourseSchedule(String courseId, String courseName) {
                // Get the current date and time
                Calendar currentTime = Calendar.getInstance();
                Date currentDate = currentTime.getTime();

                // Query Firestore to retrieve the course schedule
                FirebaseFirestore.getInstance().collection("courses")
                        .document(courseId)
                        .collection("schedule")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            // Iterate through each session in the schedule
                            for (QueryDocumentSnapshot sessionDoc : queryDocumentSnapshots) {
                                // Extract session details
                                Date sessionDate = sessionDoc.getDate("date");
                                String sessionTime = sessionDoc.getString("time");

                                // Combine session date and time to create a single Date object
                                Calendar sessionDateTime = Calendar.getInstance();
                                sessionDateTime.setTime(sessionDate);
                                // Parse session time to get hours and minutes
                                String[] timeParts = sessionTime.split(":");
                                int hours = Integer.parseInt(timeParts[0]);
                                int minutes = Integer.parseInt(timeParts[1]);
                                sessionDateTime.set(Calendar.HOUR_OF_DAY, hours);
                                sessionDateTime.set(Calendar.MINUTE, minutes);

                                // Compare session date and time with the current date and time
                                if (currentTime.before(sessionDateTime)) {
                                    // If the current time is before the session time, stop checking further sessions
                                    break;
                                } else if (currentTime.get(Calendar.YEAR) == sessionDateTime.get(Calendar.YEAR)
                                        && currentTime.get(Calendar.MONTH) == sessionDateTime.get(Calendar.MONTH)
                                        && currentTime.get(Calendar.DAY_OF_MONTH) == sessionDateTime.get(Calendar.DAY_OF_MONTH)
                                        && currentTime.get(Calendar.HOUR_OF_DAY) == sessionDateTime.get(Calendar.HOUR_OF_DAY)
                                        && currentTime.get(Calendar.MINUTE) == sessionDateTime.get(Calendar.MINUTE)) {
                                    // If the current date and time match the session date and time, mark attendance
                                    markAttendance(courseId, sessionDateTime.getTime());
                                    break; // No need to check further sessions
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Toast.makeText(QRScanner.this, "Failed to retrieve course schedule", Toast.LENGTH_SHORT).show();
                        });
            }

            // Method to mark attendance for enrolled students
            private void markAttendance(String courseId, Date sessionDateTime) {
                // Query Firestore to get enrolled students for the course
                FirebaseFirestore.getInstance().collection("courses")
                        .document(courseId)
                        .collection("enrolledStudents")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            // Iterate through each enrolled student
                            for (QueryDocumentSnapshot studentDoc : queryDocumentSnapshots) {
                                // Retrieve student ID and mark attendance for the session
                                String studentId = studentDoc.getId();
                                // You can update the Firestore database to mark attendance for the session
                                // For example, you can add a new document in the attendance collection for the session
                                // with the student ID and session date/time
                                FirebaseFirestore.getInstance().collection("courses")
                                        .document(courseId)
                                        .collection("attendance")
                                        .document(studentId + "_" + sessionDateTime.getTime())
                                        .set(new Attendance(studentId, sessionDateTime))
                                        .addOnSuccessListener(aVoid -> {
                                            // Handle success
                                            Toast.makeText(QRScanner.this, "Attendance marked for " + studentId, Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure
                                            Toast.makeText(QRScanner.this, "Failed to mark attendance for " + studentId, Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Toast.makeText(QRScanner.this, "Failed to retrieve enrolled students", Toast.LENGTH_SHORT).show();
                        });
            }

        });
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // Check if the permission request is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, start scanning
                startScanning();
            } else {
                // Permission is denied, display a message or handle it accordingly
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start camera preview when the activity resumes
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause camera preview when the activity is paused
        barcodeView.pause();
    }
}
