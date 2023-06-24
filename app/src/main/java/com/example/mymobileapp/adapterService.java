package com.example.mymobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class adapterService extends RecyclerView.Adapter<adapterService.MyViewHolder> {

    private final interfaceserv interfaceserv;
    Context context;
    ArrayList<serviceModel> serviceModelArrayList;
    private List<String> mItems = new ArrayList<>();


/*
    public void MyAdapter() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collection = firestore.collection("myCollection");

        collection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    Toast.makeText(context, "No data!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (snapshot != null) {
                    for (DocumentChange change : snapshot.getDocumentChanges()) {
                        if (change.getType() == DocumentChange.Type.ADDED) {
                            mItems.add(change.getDocument().getString("Name"));
                            notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }


 */

    public void filterList(ArrayList<serviceModel> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        serviceModelArrayList = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    public adapterService(Context context, ArrayList<serviceModel> serviceModelArrayList,
                          interfaceserv interfaceserv) {
        this.context = context;
        this.serviceModelArrayList = serviceModelArrayList;
        this.interfaceserv = interfaceserv;
    }

    @NonNull
    @Override
    public adapterService.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_vetclinic,parent,false);

        return new MyViewHolder(v, interfaceserv);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterService.MyViewHolder holder, int position) {

        serviceModel serviceModel = serviceModelArrayList.get(position);

        holder.name.setText(serviceModel.Name);
        holder.price.setText(serviceModel.Price);
        holder.description.setText(serviceModel.Description);
        //holder.stock.setText(serviceModel.Stock);
        Glide.with(context).load(serviceModel.servimglink).into(holder.imglink);
        Glide.with(context).load(serviceModel.prodimglink).into(holder.imglink2);



    }

    @Override
    public int getItemCount() {
        return serviceModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, description, stock;
        ImageView imglink, imglink2;

        public MyViewHolder(@NonNull View itemView, interfaceserv interfaceserv) {
            super(itemView);

            name = itemView.findViewById(R.id.srv_name);
            price = itemView.findViewById(R.id.srv_price);
            imglink = itemView.findViewById(R.id.srv_img);
            imglink2 = itemView.findViewById(R.id.srv_img2);
            description = itemView.findViewById(R.id.srv_description);
            //stock = itemView.findViewById(R.id.srv_stock);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(interfaceserv !=null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            interfaceserv.onItemClick(pos);
                        }
                    }

                }
            });

        }
    }
}
