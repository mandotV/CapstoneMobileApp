package com.example.mymobileapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class receiptAdapter extends RecyclerView.Adapter<receiptAdapter.ViewHolder> {

    private List<DocumentSnapshot> receiptDocs;
    private OnItemClickListener listener;


    public receiptAdapter(List<DocumentSnapshot> receiptDocs, OnItemClickListener listener) {

        this.receiptDocs = receiptDocs;
        this.listener = listener;

    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot);
    }


    @NonNull
    @Override
    public receiptAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull receiptAdapter.ViewHolder holder, int position) {

        DocumentSnapshot document = receiptDocs.get(position);
        holder.payType.setText(document.getString("payment"));
        holder.datePaid.setText(document.getString("date paid"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(document);
            }
        });

    }

    @Override
    public int getItemCount() {
        return receiptDocs.size();
    }





    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView payType, datePaid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            payType = itemView.findViewById(R.id.paymentType);
            datePaid = itemView.findViewById(R.id.date_paid);

        }

    }
}
