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

public class adapterVeter extends RecyclerView.Adapter<adapterVeter.MyViewHolder>{

    private List<DocumentSnapshot> veterDocs;
    private adapterVeter.OnItemClickListener listener;
    private Context context;

    public adapterVeter(List<DocumentSnapshot> veterDocs, adapterVeter.OnItemClickListener listener, Context context) {

        this.veterDocs = veterDocs;
        this.listener = listener;
        this.context = context;

    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot);
    }


    @NonNull
    @Override
    public adapterVeter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vets, parent, false);
        return new adapterVeter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterVeter.MyViewHolder holder, int position) {


        DocumentSnapshot document = veterDocs.get(position);
        holder.vetName.setText(document.getString("Name"));
        holder.vetCont.setText(document.getString("Number"));
        Glide.with(context).load(document.getString("vetimglink")).into(holder.vetImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(document);
            }
        });
    }

    @Override
    public int getItemCount() {
        return veterDocs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView vetName, vetCont;
        ImageView vetImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            vetName = itemView.findViewById(R.id.vet_name);
            vetCont = itemView.findViewById(R.id.vet_cnumber);
            vetImage = itemView.findViewById(R.id.vet_img);
        }
    }
}
