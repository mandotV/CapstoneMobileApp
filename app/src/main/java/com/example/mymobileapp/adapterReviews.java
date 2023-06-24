package com.example.mymobileapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class adapterReviews extends RecyclerView.Adapter<adapterReviews.ViewHolder>{

    private List<DocumentSnapshot> rev_documents;
    //int star2 = 0;

    public adapterReviews(List<DocumentSnapshot> rev_documents) {
        this.rev_documents = rev_documents;
    }

    @NonNull
    @Override
    public adapterReviews.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.showratings, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull adapterReviews.ViewHolder holder, int position) {

        DocumentSnapshot document = rev_documents.get(position);
        holder.rev_rates.setText(document.getString("rates"));
        holder.rev_reviews.setText(document.getString("reviews"));
        float new_rb = Float.parseFloat(document.getString("rates"));


        int[][] states = new int[][]{
                new int[]{android.R.attr.state_active}
        };
        int[] colors = new int[] {
                Color.parseColor("#FF0000"), // low rating
                Color.parseColor("#FFFF00"), // medium rating
                Color.parseColor("#00FF00") // pressed
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        if (new_rb < 3) {
            // Low rating: set the color to red
            holder.rev_rb.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
        } else if (new_rb < 4) {
            // Medium rating: set the color to yellow
            holder.rev_rb.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FFFF00")));
        } else {
            // High rating: set the color to green
            holder.rev_rb.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#00FF00")));
        }

        holder.rev_rb.setRating(new_rb);





        //holder.invi_rate.setText(document.getString("rates"));
        /*
        String star = document.getString("rates");
        double value = Double.parseDouble(star);
        star2 = (int) Math.round(value);

        star2 = getRating(position);

        seeReviewStore(star2);

         */

    }
/*
    public int getRating(int position)
    {
        return star2;
    }

 */

    @Override
    public int getItemCount() {
        return rev_documents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView rev_reviews, rev_rates;
        RatingBar rev_rb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rev_rates = itemView.findViewById(R.id.show_rates);
            rev_reviews = itemView.findViewById(R.id.show_reviews);
            rev_rb = itemView.findViewById(R.id.showrates_rb);
            //invi_rate = itemView.findViewById(R.id.invi_rate);

        }
    }
}
