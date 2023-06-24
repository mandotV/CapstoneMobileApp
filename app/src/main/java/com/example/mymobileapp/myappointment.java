package com.example.mymobileapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class myappointment extends AppCompatActivity implements interfacestore {


    RecyclerView recyclerView;
    ArrayList<petstore_data> petstore_dataArrayList;
    adapterPetstore adapterPetstore;

    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String email = firebaseUser.getEmail();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myappointment);

        ImageView appback = findViewById(R.id.app_back);
        ImageView appHisto = findViewById(R.id.app_history);

        appback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(myappointment.this, Home.class));
            }
        });

        appHisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    appointmentHistory();
            }
        });

        recyclerView = findViewById(R.id.recycler_appo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        petstore_dataArrayList = new ArrayList<petstore_data>();
        adapterPetstore = new adapterPetstore(myappointment.this, petstore_dataArrayList, this);

        recyclerView.setAdapter(adapterPetstore);

        showAppointment();
    }

    public void appointmentHistory()
    {
        CollectionReference collection = fs.collection("Petlover")
                .document(email).collection("medical-history");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Medical History");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.medicalhistorecycle, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.appHistoryRecycler);


        //DocumentAdapter adapter = new DocumentAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        collection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        // get the list of documents from the query snapshot
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        // create a new DocumentAdapter with the documents list
                        DocumentAdapter2 adapter = new DocumentAdapter2(documents, myappointment.this);

                        // set the adapter of the RecyclerView
                        recyclerView.setAdapter(adapter);


                    }
                });

        builder.setNegativeButton("back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        //recyclerView.setAdapter(adapter);

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showAppointment(){

        fs.collection("Petlover").document(email).collection("appointments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.e("Error", error.getMessage());
                            return;
                        }
                        if(value == null)
                        {
                            Toast.makeText(myappointment.this, "No data!", Toast.LENGTH_SHORT).show();
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {

                            if(dc.getType() == DocumentChange.Type.REMOVED)
                            {
                                int index = dc.getOldIndex();
                                petstore_dataArrayList.remove(index);
                                adapterPetstore.notifyItemRemoved(index);
                            }

                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                petstore_dataArrayList.add(dc.getDocument().toObject(petstore_data.class));
                            }

                            adapterPetstore.notifyDataSetChanged();


                        }

                    }
                });
    }

    @Override
    public void onItemClick(int pos) {

        Toast.makeText(this, "Service #" + pos, Toast.LENGTH_SHORT).show();

        /*
        String id = fs.collection("Petlover").document(email)
                .collection("cart").document().getId();
         */

        fs.collection("Petlover").document(email)
                .collection("appointments").document("" + pos)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        //String id = documentSnapshot.getId();

                        Intent intent = new Intent(myappointment.this, reservation2.class);
                        intent.putExtra("service name", petstore_dataArrayList.get(pos).name);

                        startActivity(intent);
                    }
                });

    }
}