package com.example.maizedisease;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signInButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Find views by their IDs
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in);

        // Set click listener for the sign-in button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });
    }

    private void signInUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in the user with email and password
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User signed in successfully
                        String userId = task.getResult().getUser().getUid();

                        // Check if the user is a farmer or an officer
                        checkUserType(userId);
                    } else {
                        // Sign-in failed
                        Toast.makeText(SignIn.this, "Sign-in failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType(String userId) {
        DatabaseReference farmersRef = firebaseDatabase.getReference("users");

        farmersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userType = dataSnapshot.child("userType").getValue(String.class);
                    if (userType != null && userType.equals("farmer")) {
                        navigateToActivity(userType);
                    } else {
                        checkOfficerType(userId);
                    }
                } else {
                    checkOfficerType(userId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignIn.this, "Failed to read user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOfficerType(String userId) {
        DatabaseReference officersRef = firebaseDatabase.getReference("users");
        officersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userType = dataSnapshot.child("userType").getValue(String.class);
                    if (userType != null && userType.equals("officer")) {
                        navigateToActivity(userType);
                    } else {
                        Toast.makeText(SignIn.this, "Invalid user type", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignIn.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignIn.this, "Failed to read user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToActivity(String userType) {
        Intent intent;
        if (userType.equals("farmer")) {
            intent = new Intent(SignIn.this, MainActivity.class);
        } else if (userType.equals("officer")) {
            intent = new Intent(SignIn.this, MessageActivity.class);
        } else {
            Toast.makeText(SignIn.this, "Invalid user type", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);
        finish();
    }
}