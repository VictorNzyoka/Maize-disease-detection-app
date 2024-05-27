package com.example.maizedisease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private Button login, admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        login = findViewById(R.id.loginButton);
        admin = findViewById(R.id.adminButton);

        // Set click listener for the login button
        login.setOnClickListener(v -> {
            // Start the MainActivity when the login button is clicked
            startActivity(new Intent(MainActivity2.this, SignIn.class));
        });

        // Set click listener for the admin button
        admin.setOnClickListener(v -> {
            // Start the AdminActivity when the admin button is clicked
            startActivity(new Intent(MainActivity2.this, AdminLogin.class));
        });
    }
}
