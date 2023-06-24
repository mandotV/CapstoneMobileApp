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

public class adapterPetstore extends RecyclerView.Adapter<adapterPetstore.MyViewHolder> {

    private final interfacestore interfacestore;
    Context context;
    ArrayList<petstore_data> petstore_dataArrayList;

    public adapterPetstore(Context context, ArrayList<petstore_data> petstore_dataArrayList
            ,interfacestore interfacestore) {
        this.context = context;
        this.petstore_dataArrayList = petstore_dataArrayList;
        this.interfacestore = interfacestore;
    }

    @NonNull
    @Override
    public adapterPetstore.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_petstore,parent,false);
        return new MyViewHolder(view, interfacestore);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterPetstore.MyViewHolder holder, int position) {

        petstore_data petstore_data = petstore_dataArrayList.get(position);

        holder.owner.setText(petstore_data.name);
        holder.price.setText(petstore_data.price);
        holder.date.setText(petstore_data.date);
        Glide.with(context).load(petstore_data.servimglink).into(holder.appointmentprof);


    }

    @Override
    public int getItemCount() {
        return petstore_dataArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView owner, date, price;
        ImageView appointmentprof;


        public MyViewHolder(@NonNull View itemView, interfacestore interfacestore) {
            super(itemView);

            owner = itemView.findViewById(R.id.rs_bn);
            price = itemView.findViewById(R.id.rs_price);
            date = itemView.findViewById(R.id.rs_date);
            appointmentprof = itemView.findViewById(R.id.appoinmentprof);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(interfacestore !=null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            interfacestore.onItemClick(pos);
                        }
                    }

                }
            });
        }
    }
}
