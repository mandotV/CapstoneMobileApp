package com.example.mymobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class adapterMsg extends RecyclerView.Adapter<adapterMsg.MyViewHolder>{

    private final interfacemsg interfacemsg;
    Context context;
    ArrayList<msgData> msgDataArrayList;

    public adapterMsg(Context context, ArrayList<msgData> msgDataArrayList,interfacemsg interfacemsg) {
        this.interfacemsg = interfacemsg;
        this.context = context;
        this.msgDataArrayList = msgDataArrayList;
    }

    @NonNull
    @Override
    public adapterMsg.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_msg,parent,false);
        return new MyViewHolder(v, interfacemsg);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        msgData msgData = msgDataArrayList.get(position);
/*
        holder.receiver.setText(msgData.receiver);
        Glide.with(context).load(msgData.pp).into(holder.pp);

 */

        if (!msgData.getStatusmobile().equalsIgnoreCase("DELETED")) {
            holder.receiver.setText(msgData.receiver);
            Glide.with(context).load(msgData.pp).into(holder.pp);
        } else {
            holder.itemView.setVisibility(View.GONE);
        }


    }




    @Override
    public int getItemCount() {
        return msgDataArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView receiver;
        ImageView pp;


        public MyViewHolder(@NonNull View itemView, interfacemsg interfacemsg) {
            super(itemView);

        receiver = itemView.findViewById(R.id.petLoverName);
        pp = itemView.findViewById(R.id.profileId);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(interfacemsg !=null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            interfacemsg.onItemClick(pos);

                        }
                    }

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if(interfacemsg !=null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            interfacemsg.onLongItemClick(pos);

                        }
                    }
                    return true;
                }
            });


        }
    }
}
