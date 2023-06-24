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
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {

    private List<DocumentSnapshot> documents;
    private Context context;

    public DocumentAdapter(List<DocumentSnapshot> documents, Context context) {

        this.documents = documents;
        this.context = context;
    }

    @NonNull
    @Override
    public DocumentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chistory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentAdapter.ViewHolder holder, int position) {

        DocumentSnapshot document = documents.get(position);
        holder.chtype.setText(document.getString("Type"));
        holder.chname.setText(document.getString("name"));
        String ty = document.getString("Type");

        if(ty.equalsIgnoreCase("Product"))
        {
            holder.chquantity.setText(document.getString("quantity"));
        }
        else
        {
            holder.chquantity.setText("1");
        }

        holder.chprice.setText(document.getString("price"));
        holder.chowner.setText(document.getString("owner"));

        Glide.with(context).load(document.getString("cartlink")).into(holder.chimg);


    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView chtype, chname, chquantity, chprice, chowner;
        ImageView chimg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chtype = itemView.findViewById(R.id.ch_type);
            chname = itemView.findViewById(R.id.ch_name);
            chquantity = itemView.findViewById(R.id.ch_quantity);
            chprice = itemView.findViewById(R.id.ch_price);
            chowner = itemView.findViewById(R.id.ch_owner);
            chimg = itemView.findViewById(R.id.ch_img);

        }
    }
}
