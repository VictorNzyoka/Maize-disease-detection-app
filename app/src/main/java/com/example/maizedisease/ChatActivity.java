package com.example.maizedisease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maizedisease.databinding.ActivityChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private ActivityChatBinding binding;
    private ImageView backButton, logoutButton;
    private String receiverUserId;
    private DatabaseReference databaseReferenceSender, databaseReferenceReceiver;
    private String senderRoom, receiverRoom;
    private MessageAdapter messageAdapter;
    private FirebaseUser currentUser;
    private String currentUserUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backButton = findViewById(R.id.back_button);
        logoutButton = findViewById(R.id.logout_button);

        backButton.setOnClickListener(v ->onBack());
        logoutButton.setOnClickListener(v ->logout());

        // Get the receiver's userId from the intent
        receiverUserId = getIntent().getStringExtra("userId");

        // Get the current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserUserId = currentUser.getUid();

        // Generate the sender and receiver room names
        senderRoom = currentUserUserId + receiverUserId; // Use userId instead of username
        receiverRoom = receiverUserId + currentUserUserId; // Use userId instead of username

        // Initialize the message adapter and recycler view
        messageAdapter = new MessageAdapter(this, currentUserUserId); // Use userId instead of username
        binding.recycler.setAdapter(messageAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the database references
        databaseReferenceSender = FirebaseDatabase.getInstance().getReference("chats").child(senderRoom);
        databaseReferenceReceiver = FirebaseDatabase.getInstance().getReference("chats").child(receiverRoom);

        // Add a value event listener to the sender's chat room
        databaseReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageAdapter.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    messageAdapter.add(messageModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e(TAG, "DatabaseError: " + error.getMessage());
            }
        });

        // Set a click listener on the send message button
        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.messageEd.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(message, currentUserUserId, receiverUserId); // Use userId instead of username
                    binding.messageEd.setText(""); // Clear the input field after sending the message
                }
            }
        });
    }

    private void sendMessage(String message, String senderUserId, String receiverUserId) {
        if (senderUserId != null && !senderUserId.isEmpty()) {
            String messageId = UUID.randomUUID().toString();
            MessageModel messageModel = new MessageModel(messageId, senderUserId, message);

            // Add the message to the adapter
            messageAdapter.add(messageModel);

            // Save the message to the sender's and receiver's chat rooms
            databaseReferenceSender.child(messageId).setValue(messageModel);
            databaseReferenceReceiver.child(messageId).setValue(messageModel);
        } else {
            // Handle the case where the senderUserId is not available
            Log.e(TAG, "senderUserId is not available");
            // You can show an error message to the user or take other appropriate actions
        }
    }
    private void logout() {
        Intent intent = new Intent(ChatActivity.this, SignIn.class);
        startActivity(intent);
    }
    public void onBack() {
        Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
        startActivity(intent);
    }
}
