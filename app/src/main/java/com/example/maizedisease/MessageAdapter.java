package com.example.maizedisease;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private Context context;
    private List<MessageModel> messageModelList;
    private static final int MESSAGE_TYPE_SENT = 1;
    private static final int MESSAGE_TYPE_RECEIVED = 2;
    private String currentUserUserId;
    private int spacing = 16;

    public MessageAdapter(Context context, String currentUserUserId) {
        this.context = context;
        this.currentUserUserId = currentUserUserId;
        this.messageModelList = new ArrayList<>();
    }

    public void add(MessageModel messageModel) {
        messageModelList.add(messageModel);
        notifyDataSetChanged();
    }

    public void clear() {
        messageModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESSAGE_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_row, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_row, parent, false);
        }
        return new MyViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessageModel messageModel = messageModelList.get(position);
        holder.messageTextView.setText(messageModel.getMessage());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.messageTextView.getLayoutParams();
        if (messageModel.getSenderId() != null && messageModel.getSenderId().equals(currentUserUserId)) {
            // Sender's message
            holder.messageTextView.setBackgroundResource(R.drawable.sender_background);
            params.gravity = Gravity.END;
        } else {
            // Receiver's message
            holder.messageTextView.setBackgroundResource(R.drawable.receiver_background);
            params.gravity = Gravity.START;
        }
        holder.messageTextView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messageModelList.get(position);
        if (message.getSenderId() != null && message.getSenderId().equals(currentUserUserId)) {
            return MESSAGE_TYPE_SENT;
        } else {
            return MESSAGE_TYPE_RECEIVED;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public MyViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(spacing, spacing, spacing, spacing);
            if (viewType == MESSAGE_TYPE_SENT) {
                params.gravity = Gravity.END;
            } else {
                params.gravity = Gravity.START;
            }
            itemView.setLayoutParams(params);
        }
    }
}
