package com.example.maizedisease;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class AdminLogin extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button signIn;
    TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        signIn = findViewById(R.id.sign_in);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Check if the entered email and password match the admin credentials
                if (email.equals("admin18@gmail.com") && password.equals("victor123")) {
                    // Redirect to the RegisterPage class
                    Intent intent = new Intent(AdminLogin.this, AdminHomepage.class);
                    startActivity(intent);
                } else {
                    // Display an error message if the credentials are incorrect
                    Toast.makeText(AdminLogin.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}