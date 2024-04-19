package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

// ViewCourse.java
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewCourse extends AppCompatActivity implements CourseAdapter.OnCourseClickListener{
    RecyclerView courseList;
    CourseAdapter adapter;
    List<String> courses;
    String courseID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        courses = new ArrayList<>();

        // Reference to the "courses" collection in Firestore
        db.collection("courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        courses.clear(); // Clear the list before adding new data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the course name from each document
                            String courseName = document.getString("name");
                            courseID = document.getId(); // Get the course ID
                            courses.add(courseName);
                        }
                        // Initialize RecyclerView and set the adapter with the list of courses
                        courseList = findViewById(R.id.courseList);
                        adapter = new CourseAdapter(courses, this); // Pass courses list without courseID
                        courseList.setLayoutManager(new LinearLayoutManager(this));
                        courseList.setAdapter(adapter);
                    } else {
                        // Handle error
                        Toast.makeText(ViewCourse.this, "Failed to load courses", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Override the onCourseClick method of the listener interface
    @Override
    public void onCourseClick(String courseName, String courseID) {
        // Start a new activity to display the list of students for the clicked course
        Intent intent = new Intent(this, EnrollStudent.class);
        intent.putExtra("courseName", courseName); // Pass the clicked course name to the new activity
        intent.putExtra("courseID", courseID);
        startActivity(intent);
    }
}

