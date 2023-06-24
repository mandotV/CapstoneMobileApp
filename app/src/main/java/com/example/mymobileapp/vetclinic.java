package com.example.mymobileapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class vetclinic extends AppCompatActivity implements interfaceserv{

    SearchView searchView;
    RecyclerView recyclerView;
    ArrayList<serviceModel> serviceModelArrayList;
    adapterService adapterService;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    String clue, usernm, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vetclinic);

        clue = getIntent().getStringExtra("clue");
        usernm = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("emailOwner");

        recyclerView = findViewById(R.id.recycler_vet);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        serviceModelArrayList = new ArrayList<serviceModel>();
        adapterService = new adapterService(vetclinic.this, serviceModelArrayList, this);

        recyclerView.setAdapter(adapterService);


        EventChangeListener();

        searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;

            }

            @Override
            public boolean onQueryTextChange(String s) {

                filter(s);
                return false;
            }
        });

    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<serviceModel> filteredlist = new ArrayList<serviceModel>();

        // running a for loop to compare elements.
        for (serviceModel item : serviceModelArrayList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            //Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapterService.filterList(filteredlist);
        }
    }

    private void EventChangeListener() {

        if(clue.equalsIgnoreCase("vetclinic"))
        {
            fs.collection("vetclinic").document(usernm).collection("services")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            if (error != null) {
                                Log.e("Error", error.getMessage());
                                return;
                            }
                            for (DocumentChange dc : value.getDocumentChanges()) {


                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    serviceModelArrayList.add(dc.getDocument().toObject(serviceModel.class));
                                }

                                adapterService.notifyDataSetChanged();


                            }

                        }
                    });
        }
        else
        {

            fs.collection("petstore").document(usernm).collection("products")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            if (error != null) {
                                Log.e("Error", error.getMessage());
                                return;
                            }
                            for (DocumentChange dc : value.getDocumentChanges()) {


                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    serviceModelArrayList.add(dc.getDocument().toObject(serviceModel.class));
                                }

                                adapterService.notifyDataSetChanged();


                            }

                        }
                    });
       }

    }


    @Override
    public void onItemClick(int pos) {


        Toast.makeText(this, "***", Toast.LENGTH_SHORT).show();

        if(clue.equalsIgnoreCase("vetclinic"))
        {
            fs.collection("vetclinic").document(usernm).collection("services")
                    .document(pos + "").get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {


                            Intent intent = new Intent(vetclinic.this, serviceDetails.class);
                            intent.putExtra("Name", serviceModelArrayList.get(pos).Name);
                            intent.putExtra("Price", serviceModelArrayList.get(pos).Price);
                            intent.putExtra("username", usernm);
                            intent.putExtra("emailOwner", email);
                            intent.putExtra("clue", clue);

                            startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(vetclinic.this, "Network Err!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
        {
            fs.collection("petstore").document(pos + "").get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {


                            Intent intent = new Intent(vetclinic.this, productsDetails.class);
                            intent.putExtra("Name", serviceModelArrayList.get(pos).Name);
                            intent.putExtra("Price", serviceModelArrayList.get(pos).Price);
                            intent.putExtra("username", usernm);
                            intent.putExtra("emailOwner", email);
                            intent.putExtra("clue", clue);
                            startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(vetclinic.this, "Network Err!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }



    }
}