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

public class adapterVet extends RecyclerView.Adapter<adapterVet.MyViewHolder> {

    private final interfacevet interfacevet;
    Context context;
    ArrayList<vetdata> vetdataArrayList;


    public adapterVet(Context context, ArrayList<vetdata> vetdataArrayList, interfacevet interfacevet) {
        this.context = context;
        this.vetdataArrayList = vetdataArrayList;
        this.interfacevet = interfacevet;
    }

    @NonNull
    @Override
    public adapterVet.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_pets,parent,false);
        return new MyViewHolder(v, interfacevet);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterVet.MyViewHolder holder, int position) {


        vetdata vetdata = vetdataArrayList.get(position);

        holder.price.setText(vetdata.Price);
        holder.category.setText(vetdata.Category);
        Glide.with(context).load(vetdata.petimglink).into(holder.petimglink);
    }

    @Override
    public int getItemCount() {
        return vetdataArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView category, price;
        ImageView petimglink;


        public MyViewHolder(@NonNull View itemView, interfacevet interfacevet) {
            super(itemView);


            petimglink = itemView.findViewById(R.id.pet_img);
            category = itemView.findViewById(R.id.pet_category);
            price = itemView.findViewById(R.id.pet_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(interfacevet !=null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            interfacevet.onItemClick(pos);
                        }
                    }

                }
            });


        }


    }

}
