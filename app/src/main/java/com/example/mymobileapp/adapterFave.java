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

public class adapterFave extends RecyclerView.Adapter<adapterFave.MyViewHolder> {

    private final interfaceFave interfaceFave;
    Context context;
    ArrayList<favedata> favedataArrayList;


    public void filterList(ArrayList<favedata> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        favedataArrayList = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    public adapterFave(Context context, ArrayList<favedata> favedataArrayList, interfaceFave interfaceFave) {
        this.context = context;
        this.favedataArrayList = favedataArrayList;
        this.interfaceFave = interfaceFave;
    }

    @NonNull
    @Override
    public adapterFave.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_fave,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterFave.MyViewHolder holder, int position) {

        favedata favedata = favedataArrayList.get(position);

        holder.brandname.setText(favedata.bname);
        holder.email.setText(favedata.email);
        holder.address.setText(favedata.address);
        holder.type.setText(favedata.Type);
        Glide.with(context).load(favedata.imglink).into(holder.ppFave);

    }

    @Override
    public int getItemCount() {
        return favedataArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView brandname, email, address, type;
        ImageView ppFave;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.fave_email);
            brandname = itemView.findViewById(R.id.fave_name);
            address = itemView.findViewById(R.id.fave_add);
            ppFave = itemView.findViewById(R.id.faveImage);
            type = itemView.findViewById(R.id.fave_type);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(interfaceFave !=null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            interfaceFave.onItemClick(pos);
                        }
                    }

                }
            });

        }
    }
}
