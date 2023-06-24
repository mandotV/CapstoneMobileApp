package com.example.mymobileapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class petstore extends AppCompatActivity implements interfacestore {

    RecyclerView recyclerView;
    ArrayList<petstore_data> petstore_dataArrayList;
    adapterPetstore adapterPetstore;
    FirebaseFirestore fs;
    CircleImageView psBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petstore);


        recyclerView = findViewById(R.id.recycler_petstore);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fs = FirebaseFirestore.getInstance();
        petstore_dataArrayList = new ArrayList<petstore_data>();
        adapterPetstore = new adapterPetstore(petstore.this, petstore_dataArrayList, this);

        recyclerView.setAdapter(adapterPetstore);

        psBack = findViewById(R.id.ps_back);

        psBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(petstore.this, Home.class));

            }
        });

        EventChangeListener();



    }

    private void EventChangeListener() {
        fs.collection("petstore")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error !=null){
                            Log.e("Error",error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()){


                            if(dc.getType() == DocumentChange.Type.ADDED){
                                petstore_dataArrayList.add(dc.getDocument().toObject(petstore_data.class));
                            }

                            adapterPetstore.notifyDataSetChanged();


                        }

                    }
                });
    }

    @Override
    public void onItemClick(int pos) {

        Toast.makeText(this, "You clicked this store", Toast.LENGTH_SHORT).show();

        fs.collection("petstore").document(pos+"").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String email = documentSnapshot.getString("email");
                String usernm = documentSnapshot.getString("username");

                Intent intent = new Intent(petstore.this, card_details.class);
                //intent.putExtra("email", petstore_dataArrayList.get(pos).email);
                //intent.putExtra("username", petstore_dataArrayList.get(pos).username);

                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(petstore.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}