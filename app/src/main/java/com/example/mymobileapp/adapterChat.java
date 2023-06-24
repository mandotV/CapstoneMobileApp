package com.example.mymobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class adapterChat extends RecyclerView.Adapter<adapterChat.MyViewHolder> {

    Context context;
    ArrayList<chatData> chatDataArrayList;


    public adapterChat(Context context, ArrayList<chatData> chatDataArrayList) {
        this.context = context;
        this.chatDataArrayList = chatDataArrayList;
    }

    @NonNull
    @Override
    public adapterChat.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.chat_adapter,parent,false);

        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        chatData chatData = chatDataArrayList.get(position);


        if(chatData.id.equalsIgnoreCase(chatData.sender))
        {

            holder.sender_layout.setVisibility(LinearLayout.VISIBLE);
            holder.message.setText(chatData.message);
            holder.timestamp.setText(chatData.timestamp);
            holder.receiver_layout.setVisibility(LinearLayout.GONE);
        }

        else
        {
            holder.sender_layout.setVisibility(LinearLayout.GONE);
            holder.message2.setText(chatData.message);
            holder.timestamp2.setText(chatData.timestamp);
            holder.receiver_layout.setVisibility(LinearLayout.VISIBLE);

        }


    }


    @Override
    public int getItemCount() {
        return chatDataArrayList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View receiver_layout, sender_layout;
        TextView receiver, message, timestamp, message2, timestamp2;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            sender_layout = itemView.findViewById(R.id.sender_layout);
            message = itemView.findViewById(R.id.sender_msg);
            timestamp = itemView.findViewById(R.id.sender_time);

            receiver_layout = itemView.findViewById(R.id.receiver_layout);
            message2 = itemView.findViewById(R.id.receiver_msg);
            timestamp2 = itemView.findViewById(R.id.receiver_time);

            receiver = itemView.findViewById(R.id.chat_name);




        }
    }
}
