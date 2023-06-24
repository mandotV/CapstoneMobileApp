package com.example.mymobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mymobileapp.databinding.ActivityCardDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;

public class card_details extends AppCompatActivity {

    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    String email;
    String usernm;

    private ActivityCardDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        email = getIntent().getStringExtra("email");
        usernm = getIntent().getStringExtra("username");
        binding.detailEmail.setText(email);
        binding.detailUsername.setText(usernm);

        showData();
        chck();

        binding.toMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(card_details.this, Msg.class));

            }
        });

        binding.toLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(card_details.this, mapMod.class));

            }
        });

        binding.toFavouriteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fs.collection("favorite").document(email+"").get().
                        addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if(documentSnapshot.exists())
                                {
                                    binding.toFavouriteList.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);

                                    fs.collection("favorite").document(email + "")
                                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    Toast.makeText(card_details.this, "Removed from favorites!", Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                }
                                else
                                {
                                    binding.toFavouriteList.setBackgroundResource(R.drawable.ic_baseline_red_24);
                                    addFave();
                                }


                            }
                        });

            }
        });






    }

    public void showData(){

        fs.collection("petstore").document(usernm +"").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        //String name = documentSnapshot.getString("brandname");
                        String address = documentSnapshot.getString("address");
                        String image = documentSnapshot.getString("imagelink");

                        //binding.detailName.setText(name);
                        binding.detailAddress.setText(address);
                        Glide.with(card_details.this).load(image)
                                .override(400,500)
                                .into(binding.detailImage);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(card_details.this, "Error!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void addFave(){

        String bname = binding.detailName.getText().toString();
        String address = binding.detailAddress.getText().toString();
        String outlet = "Pet Store";


        HashMap hashMap = new HashMap();
        hashMap.put("email", email);
        hashMap.put("brandname", bname);
        hashMap.put("address", address);
        hashMap.put("outlet", outlet);


        fs.collection("favorite").document( email + "").set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Toast.makeText(card_details.this, "Added to Favorites!", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(card_details.this, "Error!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void chck(){

        fs.collection("favorite").document(email +"").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists()){

                            binding.toFavouriteList.setBackgroundResource(R.drawable.ic_baseline_red_24);

                        }
                        else
                        {
                            binding.toFavouriteList.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                            //Toast.makeText(card_details2.this, "No favorite existed!", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }
}