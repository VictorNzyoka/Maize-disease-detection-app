package com.example.maizedisease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.maizedisease.databinding.ActivityMessageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "MessageActivity";
    private ActivityMessageBinding binding;
    private DatabaseReference farmersReference, officersReference;
    private UserAdapter userAdapter;
    private String currentUserType;
    private ImageView backButton, logoutButton;
    private FirebaseUser currentUser;
    private Set<String> userIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize RecyclerView and adapter
        userAdapter = new UserAdapter(this);
        binding.recycler.setAdapter(userAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        backButton = findViewById(R.id.back_button);
        logoutButton = findViewById(R.id.logout_button);

        backButton.setOnClickListener(v ->onBack());
        logoutButton.setOnClickListener(v ->logout());

        // Add item decoration for spacing
        binding.recycler.addItemDecoration(new SpacesItemDecoration(20, 20));

        // Initialize Firebase references
        farmersReference = FirebaseDatabase.getInstance().getReference().child("farmers");
        officersReference = FirebaseDatabase.getInstance().getReference().child("officers");

        // Get the current user's type and userId
        getCurrentUserTypeAndUserId();

        // Initialize userIds set
        userIds = new HashSet<>();
    }

    private void getCurrentUserTypeAndUserId() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseDatabase.getInstance().getReference().child("users")
                    .child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            currentUserType = snapshot.child("userType").getValue(String.class);
                            readUserData();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle possible errors
                            Log.e(TAG, "DatabaseError: " + error.getMessage());
                            showErrorToast("Error: " + error.getMessage());
                        }
                    });
        }
    }

    private void readUserData() {
        userAdapter.clearUsers();
        userIds.clear();

        if (currentUserType != null && currentUserType.equals("farmer")) {
            // If the current user is a farmer, display officers
            readOfficersData();
        } else {
            // If the current user is an officer, display farmers
            readFarmersData();
        }
    }

    private void readFarmersData() {
        farmersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readUserDataFromSnapshot(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                Log.e(TAG, "DatabaseError: " + error.getMessage());
                showErrorToast("Error: " + error.getMessage());
            }
        });
    }

    private void readOfficersData() {
        officersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readUserDataFromSnapshot(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                Log.e(TAG, "DatabaseError: " + error.getMessage());
                showErrorToast("Error: " + error.getMessage());
            }
        });
    }

    private void readUserDataFromSnapshot(DataSnapshot snapshot) {
        // Iterate through user data
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            String userId = dataSnapshot.getKey();

            // Exclude current user's data
            if (userId != null && !userId.equals(currentUser.getUid())) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                // Add user to adapter if userModel is not null
                if (userModel != null) {
                    if (!userIds.contains(userModel.getUserId())) {
                        userIds.add(userModel.getUserId());
                        userAdapter.addUser(userModel);
                    }
                } else {
                    // Log if deserialization fails
                    Log.e(TAG, "Failed to deserialize UserModel for userId: " + userId);
                    showErrorToast("Error deserializing data for userId: " + userId);
                }
            }
        }
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void logout() {
        Intent intent = new Intent(MessageActivity.this, SignIn.class);
        startActivity(intent);
    }

    private void onBack() {
        Intent intent = new Intent(MessageActivity.this, SignIn.class);
        startActivity(intent);
    }

}