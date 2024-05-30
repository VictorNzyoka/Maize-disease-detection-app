package com.example.maizedisease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminHomepage extends AppCompatActivity {

    private Button RegisterFarmer,RegisterOfficer;
    private ImageView backButton, logoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_homepage);

        RegisterFarmer = findViewById(R.id.RegisterFarmerButton);
        RegisterOfficer = findViewById(R.id.RegisterOfficerButton);
        backButton = findViewById(R.id.back_button);
        logoutButton = findViewById(R.id.logout_button);


        RegisterFarmer.setOnClickListener(v ->RegisterFarmers());
        RegisterOfficer.setOnClickListener(v ->RegisterOfficers());
        backButton.setOnClickListener(v ->onBack());
        logoutButton.setOnClickListener(v ->logout());

    }

    private void logout() {
        Intent intent = new Intent(AdminHomepage.this, AdminLogin.class);
        startActivity(intent);
    }
    public void onBack() {
        Intent intent = new Intent(AdminHomepage.this, AdminLogin.class);
        startActivity(intent);
    }

    private void RegisterOfficers() {
        Intent intent = new Intent(AdminHomepage.this, RegisterOfficerActivity.class);
        startActivity(intent);
    }

    private void RegisterFarmers() {
        Intent intent = new Intent(AdminHomepage.this, RegisterFarmerActivity.class);
        startActivity(intent);
    }
}