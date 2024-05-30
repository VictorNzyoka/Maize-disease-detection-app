package com.example.maizedisease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {
    private RecyclerView fungicideRecyclerView;
    private FungicideAdapter fungicideAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private ImageView backButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialize views
        TextView outputTextView = findViewById(R.id.outputTextView);
        fungicideRecyclerView = findViewById(R.id.fungicideRecyclerView);
        backButton = findViewById(R.id.back_button);
        logoutButton = findViewById(R.id.logout_button);

        backButton.setOnClickListener(v ->onBack());
        logoutButton.setOnClickListener(v ->logout());

        drawerLayout = findViewById(R.id.drawableLayout);
        if (drawerLayout != null) {
            toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                // Handle the case where the ActionBar is null
                Log.e("ResultsActivity", "ActionBar is null");
                Toast.makeText(this, "ActionBar is not available", Toast.LENGTH_SHORT).show();
            }

            NavigationView navigationView = findViewById(R.id.nav_views);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // Handle navigation item selection
                    drawerLayout.closeDrawer(navigationView);
                    return true;
                }
            });
        } else {
            // Handle the case where drawerLayout is null
            Log.e("ResultsActivity", "DrawerLayout is null");
            Toast.makeText(this, "DrawerLayout is not available", Toast.LENGTH_SHORT).show();
        }

        // Retrieve prediction result and fungicide list from intent
        String predictionResult = getIntent().getStringExtra("ResultActivity");
        ArrayList<FungicideModel> fungicideList = getIntent().getParcelableArrayListExtra("fungicideList");
        if (fungicideList == null) {
            fungicideList = new ArrayList<>();
        }

        // Set the prediction result to the outputTextView
        if (predictionResult != null) {
            outputTextView.setText(predictionResult);
        }

        // Set up the RecyclerView and adapter
        fungicideAdapter = new FungicideAdapter(fungicideList);
        fungicideRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fungicideRecyclerView.setAdapter(fungicideAdapter);
    }

    private void logout() {
        Intent intent = new Intent(ResultsActivity.this, SignIn.class);
        startActivity(intent);
    }

    private void onBack() {
        Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (toggle != null) {
            toggle.syncState();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event
        return toggle != null && toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}