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
        String userType = farmerRadioButton.isChecked() ? "farmer" : officerRadioButton.isChecked() ? "officer" : null;

        // Check if user type is selected
        if (userType == null) {
            Toast.makeText(RegisterPage.this, "Please select user type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user account in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User account created successfully
                        String userId = task.getResult().getUser().getUid();
                        Log.d(TAG, "User registered successfully: " + userId);
                        Toast.makeText(RegisterPage.this, "Registration successful", Toast.LENGTH_SHORT).show();

                        // Store user data in Firebase Realtime Database
                        RegistrationModel registrationModel = new RegistrationModel(userId, username, email, phoneNumber, userType, password);
                        storeUserData(registrationModel);
                    } else {
                        // User registration failed
                        handleRegistrationFailure(task.getException());
                    }
                });
    }

    private void storeUserData(RegistrationModel registrationModel) {
        String userId = registrationModel.getUserId();
        String userType = registrationModel.getUserType();

        DatabaseReference userRef = firebaseDatabase.getReference("users").child(userId);
        UserModel userModel = new UserModel(userId, registrationModel.getUsername(), registrationModel.getEmail(), registrationModel.getPhoneNumber(), userType);

        userRef.setValue(userModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User data stored successfully");
            } else {
                Log.e(TAG, "Failed to store user data", task.getException());
            }
        });

        // Store user data under farmers or officers node based on userType
        if (userType.equals("farmer")) {
            farmersRef.child(userId).setValue(userModel);
        } else {
            officersRef.child(userId).setValue(userModel);
        }
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
            Toast.makeText(RegisterPage.this, "User already exists", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Log the exception and stack trace
            Log.e(TAG, "Registration failed", e);

            // Display an error message
            Toast.makeText(RegisterPage.this, "Registration failed: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}