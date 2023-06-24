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

public class DocumentAdapter2 extends RecyclerView.Adapter<DocumentAdapter2.ViewHolder> {

    private List<DocumentSnapshot> documents;
    private Context context;

    public DocumentAdapter2(List<DocumentSnapshot> documents, Context context) {

        this.documents = documents;
        this.context = context;
    }

    @NonNull
    @Override
    public DocumentAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apphistor, parent, false);
        return new DocumentAdapter2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentAdapter2.ViewHolder holder, int position) {

        DocumentSnapshot document = documents.get(position);
        holder.appVet.setText(document.getString("vet"));
        holder.appName.setText(document.getString("name"));
        holder.appPrice.setText(document.getString("price"));
        holder.appOwner.setText(document.getString("owner"));

        Glide.with(context).load(document.getString("servimglink")).into(holder.appImg);


    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView appVet, appName, appPrice, appOwner;
        ImageView appImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            appVet = itemView.findViewById(R.id.apph_vet);
            appName = itemView.findViewById(R.id.apph_name);
            appPrice = itemView.findViewById(R.id.apph_price);
            appOwner = itemView.findViewById(R.id.apph_owner);

            appImg = itemView.findViewById(R.id.apph_img);

        }
    }
}
