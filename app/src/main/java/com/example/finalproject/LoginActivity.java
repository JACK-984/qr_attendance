    package com.example.finalproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

    public class LoginActivity extends AppCompatActivity {
        Button loginButton;
        FirebaseAuth mAuth;
        TextInputEditText emailLogin, passwordLogin;
        @Override
        public void onStart() {
            super.onStart();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null){
                // User is signed in
                checkUserRoleAndRedirect(currentUser.getUid());
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login_activity);
            loginButton = findViewById(R.id.button);
            emailLogin = findViewById(R.id.userLogin);
            passwordLogin = findViewById(R.id.passwordLogin);
            mAuth = FirebaseAuth.getInstance();

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = String.valueOf(emailLogin.getText());
                    String password = String.valueOf(passwordLogin.getText());
                    if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                        Toast.makeText(LoginActivity.this,"Enter all the fields",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        checkUserRoleAndRedirect(user.getUid());
                                    } else {
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });

        }

        private void checkUserRoleAndRedirect(String userId) {
            getUserRoleFromDatabase(userId, new UserRoleCallback() {
                @Override
                public void onCallback(String role) {
                    if (role.equals("student")) {
                        startActivity(new Intent(getApplicationContext(), StudentActivity.class));
                    }
                    else if(role.equals("faculty")){
                        // Redirect to another activity based on other roles
                        // For example:
                         startActivity(new Intent(getApplicationContext(), FacultyActivity.class));
                    }
                    finish();
                }
            });
        }

        // Interface to handle callback for user role retrieval
        private interface UserRoleCallback {
            void onCallback(String role);
        }

        // Example method to retrieve user role from Firebase Firestore or Realtime Database
        private void getUserRoleFromDatabase(String userId, UserRoleCallback callback) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereEqualTo("userID", userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // Assuming there's only one document per user, retrieve the first document
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                String role = documentSnapshot.getString("role");
                                callback.onCallback(role);
                            } else {
                                // Document for the user does not exist
                                // Handle this case accordingly
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error occurred while fetching user role
                            // Handle this case accordingly
                        }
                    });
        }
    }
