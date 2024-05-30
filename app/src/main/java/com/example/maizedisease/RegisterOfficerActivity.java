package com.example.maizedisease;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterOfficerActivity extends AppCompatActivity {

    private static final String TAG = "RegisterOfficerActivity";
    private static final String USER_TYPE = "officer";

    private EditText emailEditText, usernameEditText, phoneNumberEditText, officerLocationEditText, experienceEditText, passwordEditText;
    private Button submitButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_officer);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");

        // Find views by their IDs
        emailEditText = findViewById(R.id.email);
        usernameEditText = findViewById(R.id.username);
        phoneNumberEditText = findViewById(R.id.phone_number);
        officerLocationEditText = findViewById(R.id.officerLocation);
        experienceEditText = findViewById(R.id.experience);
        passwordEditText = findViewById(R.id.password);
        submitButton = findViewById(R.id.submit);

        // Set click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    registerUser(USER_TYPE);
                }
            }
        });
    }

    private boolean validateInput() {
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String officerLocation = officerLocationEditText.getText().toString().trim();
        String experience = experienceEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            emailEditText.requestFocus();
            return false;
        }

        // Validate password strength
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        // Validate username, phone number, location, and experience
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required");
            usernameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberEditText.setError("Phone number is required");
            phoneNumberEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(officerLocation)) {
            officerLocationEditText.setError("Location is required");
            officerLocationEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(experience)) {
            experienceEditText.setError("Experience is required");
            experienceEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void registerUser(String userType) {
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String officerLocation = officerLocationEditText.getText().toString().trim();
        int experience = Integer.parseInt(experienceEditText.getText().toString().trim());
        String password = passwordEditText.getText().toString().trim();

        // Create user account in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User account created successfully
                        String userId = task.getResult().getUser().getUid();
                        Log.d(TAG, "Officer registered successfully: " + userId);
                        Toast.makeText(RegisterOfficerActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                        // Store officer data in Firebase Realtime Database
                        OfficerModel officerModel = new OfficerModel(userId, username, email, phoneNumber, officerLocation, experience, userType);
                        storeUserData(officerModel);
                    } else {
                        // User registration failed
                        handleRegistrationFailure(task.getException());
                    }
                });
    }

    private void storeUserData(OfficerModel officerModel) {
        String userId = officerModel.getUserId();
        usersRef.child(userId).setValue(officerModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User data stored successfully");
            } else {
                Log.e(TAG, "Failed to store user data", task.getException());
            }
        });
    }

    private void handleRegistrationFailure(Exception exception) {
        try {
            throw exception;
        } catch (FirebaseAuthWeakPasswordException e) {
            passwordEditText.setError("Weak password");
            passwordEditText.requestFocus();
        } catch (FirebaseAuthInvalidCredentialsException e) {
            emailEditText.setError("Invalid email");
            emailEditText.requestFocus();
        } catch (FirebaseAuthUserCollisionException e) {
            // This user already exists, handle appropriately
            Toast.makeText(RegisterOfficerActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Log the exception and stack trace
            Log.e(TAG, "Registration failed", e);

            // Display an error message
            Toast.makeText(RegisterOfficerActivity.this, "Registration failed: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
