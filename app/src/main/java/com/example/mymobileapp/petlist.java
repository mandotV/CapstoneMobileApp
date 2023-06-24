package com.example.mymobileapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class petlist extends AppCompatActivity implements interfacevet {

    RecyclerView recyclerView;
    ArrayList<vetdata> vetdataArrayList;
    adapterVet adapterVet;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();

    String usernm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petlist);

        usernm = getIntent().getStringExtra("username");

        recyclerView = findViewById(R.id.recycler_pet);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vetdataArrayList = new ArrayList<vetdata>();
        adapterVet = new adapterVet(petlist.this, vetdataArrayList, this);

        recyclerView.setAdapter(adapterVet);

        EventChangeListener();
    }

    private void EventChangeListener() {


            fs.collection("petstore").document(usernm).collection("pets")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            if (error != null) {
                                Log.e("Error", error.getMessage());
                                return;
                            }
                            for (DocumentChange dc : value.getDocumentChanges()) {


                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    vetdataArrayList.add(dc.getDocument().toObject(vetdata.class));
                                }

                                adapterVet.notifyDataSetChanged();


                            }

                        }
                    });
        }

    @Override
    public void onItemClick(int pos) {

        fs.collection("petstore").document(usernm).collection("pets")
                .document("" + pos)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                        Intent intent = new Intent(petlist.this, pet_details.class);
                        intent.putExtra("Name", vetdataArrayList.get(pos).Name);
                        intent.putExtra("Price", vetdataArrayList.get(pos).Price);
                        intent.putExtra("petimglink", vetdataArrayList.get(pos).petimglink);
                        intent.putExtra("username", usernm);
                        startActivity(intent);
                    }
                });


    }
}