package com.example.maizedisease;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    private List<UserModel> userModelList;

    public UserAdapter(Context context) {
        this.context = context;
        userModelList = new ArrayList<>();
    }

    public void addUser(UserModel userModel) {
        userModelList.add(userModel);
        notifyItemInserted(userModelList.size() - 1);
    }

    public void clearUsers() {
        int size = userModelList.size();
        userModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        holder.bind(userModel);
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView usernameTextView, emailTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.userName);
            emailTextView = itemView.findViewById(R.id.userEmail);
            itemView.setOnClickListener(this);
        }

        public void bind(UserModel userModel) {
            usernameTextView.setText(userModel.getUsername());
            emailTextView.setText(userModel.getEmail());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                UserModel userModel = userModelList.get(position);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userId", userModel.getUserId());
                context.startActivity(intent);
            }
        }
    }
}