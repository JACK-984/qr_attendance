package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentViewCourses extends AppCompatActivity {
    RecyclerView attendanceRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view_courses);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        attendanceRecycler = findViewById(R.id.recyclerView);
        attendanceRecycler.setLayoutManager(new LinearLayoutManager(this));
        // Query the collection "users" where userID field equals the current user's ID
        db.collection("users")
                .whereEqualTo("userID", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // If there's a document matching the current user's ID
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the first document (assuming there's only one matching document)
                        QueryDocumentSnapshot userDocument = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);

                        // Retrieve the user's subcollection "attendance_history"
                        db.collection("users")
                                .document(userDocument.getId()) // Use the document ID of the user
                                .collection("attendance_history")
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                    Log.d("AttendanceDebug", "Number of documents retrieved: " + queryDocumentSnapshots1.size());

                                    // Process the documents in the subcollection
                                    List<AttendanceHistory> attendanceList = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots1) {
                                        AttendanceHistory attendanceItem = document.toObject(AttendanceHistory.class);
                                        attendanceList.add(attendanceItem);
                                    }
                                    // Pass the attendanceList to your RecyclerView adapter to display it
                                    AttendanceHistoryAdapter adapter = new AttendanceHistoryAdapter(attendanceList);
                                    attendanceRecycler.setAdapter(adapter);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    Toast.makeText(StudentViewCourses.this, "Failed to retrieve attendance history", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Handle case where there's no user document matching the current user's ID
                        Toast.makeText(StudentViewCourses.this, "User document not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(StudentViewCourses.this, "Failed to retrieve user document", Toast.LENGTH_SHORT).show();
                });
    }



}