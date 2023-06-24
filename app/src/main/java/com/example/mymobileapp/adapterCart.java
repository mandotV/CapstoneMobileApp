package com.example.mymobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class adapterCart extends RecyclerView.Adapter<adapterCart.MyViewHolder> {

    private final interfacecart interfacecart;
    Context context;
    ArrayList<cartData> cartArraylist;

    public adapterCart(Context context, ArrayList<cartData> cartArraylist
            ,interfacecart interfacecart) {
        this.context = context;
        this.cartArraylist = cartArraylist;
        this.interfacecart = interfacecart;
    }

    @NonNull
    @Override
    public adapterCart.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_cart,parent,false);
        return new MyViewHolder(view, interfacecart);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        cartData cartData = cartArraylist.get(position);

        holder.price.setText(cartData.price);
        holder.name.setText(cartData.name);
        Glide.with(context).load(cartData.cartlink).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return cartArraylist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView price, name;
        ImageView image;
        //ImageButton del;


        public MyViewHolder(@NonNull View itemView, interfacecart interfacecart) {
            super(itemView);

            image = itemView.findViewById(R.id.cartPic);
            price = itemView.findViewById(R.id.cartPrice);
            name = itemView.findViewById(R.id.cart_name);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(interfacecart !=null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            interfacecart.onItemClick(pos);
                        }
                    }

                }
            });
        }
    }
}
