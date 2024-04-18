package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CreateCourse extends AppCompatActivity {
    private EditText editTextCourseName, editTextCourseCode, editTextStartTime, editTextEndTime;
    private CheckBox checkBoxMonday, checkBoxTuesday, checkBoxWednesday, checkBoxThursday, checkBoxFriday, checkBoxSaturday;
    private Button buttonCreateCourse;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        // Initialize Firestore and Firebase Storage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize views
        editTextCourseName = findViewById(R.id.courseName);
        editTextCourseCode = findViewById(R.id.courseCode);
        editTextStartTime = findViewById(R.id.start_time);
        editTextEndTime = findViewById(R.id.end_time);
        checkBoxMonday = findViewById(R.id.checkBox_monday);
        checkBoxTuesday = findViewById(R.id.checkBox_tuesday);
        checkBoxWednesday = findViewById(R.id.checkBox_wednesday);
        checkBoxThursday = findViewById(R.id.checkBox_thursday);
        checkBoxFriday = findViewById(R.id.checkBox_friday);
        checkBoxSaturday = findViewById(R.id.checkBox_saturday);
        buttonCreateCourse = findViewById(R.id.button_create_course);

        // Set click listener for the create course button
        buttonCreateCourse.setOnClickListener(v -> createCourse());
    }

    private void createCourse() {
        // Retrieve input data from EditText fields
        String courseName = editTextCourseName.getText().toString().trim();
        String courseCode = editTextCourseCode.getText().toString().trim();
        String startTime = editTextStartTime.getText().toString().trim();
        String endTime = editTextEndTime.getText().toString().trim();
        boolean[] daysOfWeek = {
                checkBoxMonday.isChecked(),
                checkBoxTuesday.isChecked(),
                checkBoxWednesday.isChecked(),
                checkBoxThursday.isChecked(),
                checkBoxFriday.isChecked(),
                checkBoxSaturday.isChecked()
        };
        // Create a List<Boolean> from the boolean array
        List<Boolean> daysOfWeekList = new ArrayList<>();
        for (boolean day : daysOfWeek) {
            daysOfWeekList.add(day);
        }
        // Generate a unique course ID
        String courseID = UUID.randomUUID().toString();

        // Get the current user ID (assuming you have a way to retrieve it)
        String userID = getCurrentUserID(); // Implement this method to get the current user's ID

        // Generate QR code
        Bitmap qrCodeBitmap = generateQRCode(courseID);

        // Upload QR code image to Firebase Storage
        uploadQRCodeImage(courseID, qrCodeBitmap);

        // Create a map to hold course data
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("name", courseName);
        courseData.put("code", courseCode);
        courseData.put("startTime", startTime);
        courseData.put("endTime", endTime);
        courseData.put("daysOfWeek", daysOfWeekList); // Use the List<Boolean> instead of boolean[]
        courseData.put("qrCodeID", courseID);
        courseData.put("createdBy", userID);

        // Upload course data to Firestore
        db.collection("courses").document(courseID)
                .set(courseData)
                .addOnSuccessListener(aVoid -> {
                    // Course created successfully
                    Toast.makeText(CreateCourse.this, "Course created successfully", Toast.LENGTH_SHORT).show();
                    // Finish the activity or navigate to another screen if needed
                })
                .addOnFailureListener(e -> {
                    // Failed to create course
                    Toast.makeText(CreateCourse.this, "Failed to create course", Toast.LENGTH_SHORT).show();
                });
    }

    // Sample method to get the current user's ID (you need to implement this according to your app's authentication system)
    private String getCurrentUserID() {
        // Implement this method to return the current user's ID
        // For example, if you're using Firebase Authentication, you can retrieve the current user's ID as follows:
         FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
         if (currentUser != null) {
             return currentUser.getUid();
         } else {
             return null;
         }
    }

    private Bitmap generateQRCode(String courseID) {
        // Generate QR code with course ID
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(courseID, BarcodeFormat.QR_CODE, 300, 300);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap qrCodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrCodeBitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return qrCodeBitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadQRCodeImage(String courseID, Bitmap qrCodeBitmap) {
        if (qrCodeBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
            StorageReference qrCodeRef = storage.getReference().child("qr_codes/" + courseID + ".png");
            qrCodeRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        // QR code image uploaded successfully
                        // Get the download URL for the QR code image and store it in Firestore
                        qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Store the download URL in Firestore document
                            db.collection("courses").document(courseID)
                                    .update("qrCodeURL", uri.toString())
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(CreateCourse.this, "QR code uploaded successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(CreateCourse.this, "Failed to update QR code URL in Firestore", Toast.LENGTH_SHORT).show());
                        });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(CreateCourse.this, "Failed to upload QR code image to Firebase Storage", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(CreateCourse.this, "QR code bitmap is null", Toast.LENGTH_SHORT).show();
        }
    }

}
