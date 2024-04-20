package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrollStudent extends AppCompatActivity {
    private RecyclerView recyclerViewUnenrolledStudents;
    private StudentAdapter adapter;
    private List<String> unenrolledStudents;
    Button updateBtn;
    private List<String> enrolledStudents; // Add a list to store enrolled students

    // Map to store the checked state of each student
    String courseID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_student);

        enrolledStudents = new ArrayList<>(); // Initialize the list of enrolled students

        updateBtn = findViewById(R.id.updateBtn);
        unenrolledStudents = new ArrayList<>(); // Initialize the list of unenrolled students
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the course ID passed from the previous activity
        String courseName = getIntent().getStringExtra("courseName");
        courseID = getIntent().getStringExtra("courseID");
        Toast.makeText(this,"courseID: "+ courseID,Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"courseName: "+ courseName,Toast.LENGTH_SHORT).show();

        // Reference to the specific course document in Firestore
        DocumentReference courseRef = db.collection("courses").document(courseID);

        // Reference to the "users" collection in Firestore and query only for students
        db.collection("users")
                .whereEqualTo("role", "student") // Filter by role "student"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        unenrolledStudents.clear(); // Clear the list before adding new data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the student name from each document
                            String studentName = document.getString("name");
                            unenrolledStudents.add(studentName);
                        }
                        // Initialize RecyclerView and set the adapter with the list of students
                        recyclerViewUnenrolledStudents = findViewById(R.id.recyclerViewUnenrolledStudents);
                        adapter = new StudentAdapter(unenrolledStudents, enrolledStudents, courseName);
                        recyclerViewUnenrolledStudents.setLayoutManager(new LinearLayoutManager(this));
                        recyclerViewUnenrolledStudents.setAdapter(adapter);
                    } else {
                        // Handle error
                        Toast.makeText(EnrollStudent.this, "Failed to load students", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set click listener for the updateBtn button
        updateBtn.setOnClickListener(v -> {
            // Check if the list of unenrolled students is empty
            if (unenrolledStudents.isEmpty()) {
                // Show a message indicating that there are no students to enroll
                Toast.makeText(EnrollStudent.this, "No students to enroll", Toast.LENGTH_SHORT).show();
                return; // Exit the method early
            }

            // Iterate over the unenrolled students list
            for (String studentName : unenrolledStudents) {
                // Check if the checkbox for the current student is checked
                if (adapter.isChecked(studentName)) {
                    // Enroll the student
                    adapter.enrollStudent(studentName);
                }
            }
        });
    }

    private class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
        private List<String> students;
        private List<String> enrolledStudents; // Add a field to store enrolled students
        private String courseName; // Add a field to store the course name

        public StudentAdapter(List<String> students, List<String> enrolledStudents, String courseName) {
            this.students = students;
            this.enrolledStudents = enrolledStudents;
            this.courseName = courseName;
        }

        @NonNull
        @Override
        public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_checkbox, parent, false);
            return new StudentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
            String studentName = students.get(position);
            holder.checkBoxStudent.setText(studentName);

            // Check the enrollment status of the student in Firestore
            FirebaseFirestore.getInstance().collection("courses")
                    .document(courseID)
                    .collection("enrolledStudents")
                    .document(studentName)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        // Set the checked state of the checkbox based on enrollment status
                        boolean isEnrolled = documentSnapshot.exists();
                        holder.checkBoxStudent.setChecked(isEnrolled);
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(EnrollStudent.this, studentName + "Is not enrolled", Toast.LENGTH_SHORT).show();
                    });
        }

        @Override
        public int getItemCount() {
            return students.size();
        }

        public class StudentViewHolder extends RecyclerView.ViewHolder {
            CheckBox checkBoxStudent;

            public StudentViewHolder(@NonNull View itemView) {
                super(itemView);
                checkBoxStudent = itemView.findViewById(R.id.checkBoxStudent);

                // Set click listener for checkbox
                checkBoxStudent.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    String studentName = students.get(getAdapterPosition());
                    if (isChecked) {
                        // Student is checked (enrolled), enroll the student
                        enrollStudent(studentName);
                    } else {
                        // Student is unchecked (unenrolled), unenroll the student
                        unenrollStudent(studentName);
                    }
                });
            }
        }

        private void enrollStudent(String studentName) {
            // Reference to the specific course document in Firestore using the courseID
            DocumentReference courseRef = FirebaseFirestore.getInstance().collection("courses").document(courseID);

            // Query Firestore to get the user document based on the student name
            FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("name", studentName)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        // Assuming there is only one document matching the student name
                        for (QueryDocumentSnapshot userDoc : querySnapshot) {
                            // Get the userID field from the user document
                            String userID = userDoc.getString("userID");

                            // Create a Map to represent the data to be stored in Firestore
                            Map<String, Object> enrolledStudentData = new HashMap<>();
                            enrolledStudentData.put("enrolled", true);
                            enrolledStudentData.put("userID", userID);

                            // Add the student to the enrolledStudents subcollection of the course document
                            courseRef.collection("enrolledStudents").document(studentName)
                                    .set(enrolledStudentData) // Set the Map as the data
                                    .addOnSuccessListener(aVoid -> {
                                        // Update UI or show success message
                                        Toast.makeText(EnrollStudent.this, studentName + " enrolled successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure and show error message
                                        Toast.makeText(EnrollStudent.this, "Failed to enroll " + studentName, Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to retrieve userID and show error message
                        Toast.makeText(EnrollStudent.this, "Failed to retrieve userID for " + studentName, Toast.LENGTH_SHORT).show();
                    });
        }




        // Method to unenroll the student from the course
        private void unenrollStudent(String studentName) {
            // Reference to the specific course document in Firestore using the courseID
            DocumentReference courseRef = FirebaseFirestore.getInstance().collection("courses").document(courseID);

            // Remove the student from the enrolledStudents collection of the course document
            courseRef.collection("enrolledStudents").document(studentName)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Update UI or show success message
                        Toast.makeText(EnrollStudent.this, studentName + " unenrolled successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure and show error message
                        Toast.makeText(EnrollStudent.this, "Failed to unenroll " + studentName, Toast.LENGTH_SHORT).show();
                    });
        }

        // Method to check if a student is enrolled
        public boolean isChecked(String studentName) {
            return enrolledStudents.contains(studentName);
        }
    }
}

