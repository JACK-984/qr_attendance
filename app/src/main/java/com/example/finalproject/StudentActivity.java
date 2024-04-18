package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class StudentActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String userID;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userID = mAuth.getUid();
        db = FirebaseFirestore.getInstance();
        Toast.makeText(getApplicationContext(),userID,Toast.LENGTH_SHORT).show();

        // match userID from students collections to userID from FireAuth
        // shows userRole
//        db.collection("students")
//                .whereEqualTo("userID", userID)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            String role = queryDocumentSnapshots.getDocuments().get(0).getString("role");
//                            // Now you have the role, you can display it using Toast
//                            Toast.makeText(getApplicationContext(), "Your role is: " + role, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getApplicationContext(), "No role found for this user", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Handle failures
//                        Toast.makeText(getApplicationContext(), "Failed to retrieve role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
    }
}