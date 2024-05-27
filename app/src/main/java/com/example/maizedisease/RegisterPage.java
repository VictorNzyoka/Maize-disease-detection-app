package com.example.maizedisease;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPage extends AppCompatActivity {

    private static final String TAG = "RegisterPage";

    private EditText emailEditText, usernameEditText, phoneNumberEditText, passwordEditText;
    private RadioGroup userTypeGroup;
    private RadioButton farmerRadioButton, officerRadioButton;
    private Button submitButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference farmersRef, officersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        farmersRef = firebaseDatabase.getReference("farmers");
        officersRef = firebaseDatabase.getReference("officers");

        // Find views by their IDs
        emailEditText = findViewById(R.id.email);
        usernameEditText = findViewById(R.id.username);
        phoneNumberEditText = findViewById(R.id.phone_number);
        passwordEditText = findViewById(R.id.password);
        userTypeGroup = findViewById(R.id.user_type_group);
        farmerRadioButton = findViewById(R.id.farmer_radio_btn);
        officerRadioButton = findViewById(R.id.officer_radio_btn);
        submitButton = findViewById(R.id.submit);

        // Set click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    registerUser();
                }
            }
        });
    }

    private boolean validateInput() {
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
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

        // Validate username and phone number
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

        // Validate radio button selection
        if (userTypeGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select user type", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String userType = farmerRadioButton.isChecked() ? "farmer" : "officer";

        // Create user account in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User account created successfully
                        String userId = task.getResult().getUser().getUid();
                        Log.d(TAG, "User registered successfully: " + userId);
                        Toast.makeText(RegisterPage.this, "Registration successful", Toast.LENGTH_SHORT).show();

                        // Store data in the appropriate node based on user type
                        if (userType.equals("farmer")) {
                            DatabaseReference newFarmerRef = farmersRef.child(userId);
                            newFarmerRef.child("email").setValue(email);
                            newFarmerRef.child("username").setValue(username);
                            newFarmerRef.child("phoneNumber").setValue(phoneNumber);
                            // Store additional farmer-specific data as needed
                        } else {
                            DatabaseReference newOfficerRef = officersRef.child(userId);
                            newOfficerRef.child("email").setValue(email);
                            newOfficerRef.child("username").setValue(username);
                            newOfficerRef.child("phoneNumber").setValue(phoneNumber);
                            // Store additional officer-specific data as needed
                        }
                    } else {
                        // User registration failed
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            passwordEditText.setError("Weak password");
                            passwordEditText.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            emailEditText.setError("Invalid email");
                            emailEditText.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            // This user already exists, handle appropriately
                            Toast.makeText(RegisterPage.this, "User already exists", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            // Log the exception and stack trace
                            Log.e(TAG, "Registration failed", e);

                            // Display an error message
                            Toast.makeText(RegisterPage.this, "Registration failed: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}