package com.example.mymobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Favourites extends AppCompatActivity implements interfaceFave{

    SearchView searchViewFave;
    RecyclerView recyclerView;
    ArrayList<favedata> favedataArrayList;
    adapterFave adapterFave;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        email = firebaseUser.getEmail();

        searchViewFave = findViewById(R.id.searchViewFave);

        recyclerView = findViewById(R.id.recycler_fave);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favedataArrayList = new ArrayList<favedata>();
        adapterFave = new adapterFave(Favourites.this, favedataArrayList, this);

        recyclerView.setAdapter(adapterFave);

        EventChangeListener();

        CircleImageView back = findViewById(R.id.fv_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Favourites.this, Home.class));
            }
        });

        searchViewFave.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;

            }

            @Override
            public boolean onQueryTextChange(String s) {

                filtered(s);
                return false;
            }
        });



    }

    private void filtered(String text) {
        // creating a new array list to filter our data.
        ArrayList<favedata> filteredlist = new ArrayList<favedata>();

        // running a for loop to compare elements.
        for (favedata item : favedataArrayList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getBname().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
            else if (item.getEmail().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
            else if (item.getAddress().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
            else
            {
                Toast.makeText(this, "No matches!", Toast.LENGTH_SHORT).show();
            }

        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            //Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapterFave.filterList(filteredlist);
        }
    }


    private void EventChangeListener() {

        fs.collection("Petlover")
                .document(email)
                .collection("favorites")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error !=null){
                            Log.e("Error",error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()){

                            if(dc.getType() == DocumentChange.Type.REMOVED)
                            {
                                int index = dc.getOldIndex();
                                favedataArrayList.remove(index);
                                adapterFave.notifyItemRemoved(index);
                            }

                            if(dc.getType() == DocumentChange.Type.ADDED){
                                favedataArrayList.add(dc.getDocument().toObject(favedata.class));
                            }

                            adapterFave.notifyDataSetChanged();


                        }

                    }
                });
    }

    @Override
    public void onItemClick(int pos) {

        Toast.makeText(this, "***", Toast.LENGTH_SHORT).show();

        /*
        CollectionReference collectType = fs.collection("Petlover").document(email)
                        .collection("favorites");

         */
        DocumentReference nextPage = fs.collection("Petlover")
                .document(email).collection("favorites")
                .document(pos + "");
        //Query query = collectType.whereEqualTo("Type", "Business");
        //Query query1 = collectType.whereEqualTo("Type", "Product");

            nextPage.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            String type = favedataArrayList.get(pos).Type;

                            if(type != null && type.equalsIgnoreCase("Product"))
                            {
                                Intent intent = new Intent(Favourites.this, productsDetails.class);
                                intent.putExtra("username", favedataArrayList.get(pos).username);
                                intent.putExtra("Name", favedataArrayList.get(pos).bname);

                                startActivity(intent);
                            }
                            else if(type != null && type.equalsIgnoreCase("Business"))
                            {
                                Intent intent = new Intent(Favourites.this, card_details2.class);
                                intent.putExtra("username", favedataArrayList.get(pos).username);
                                intent.putExtra("Name", favedataArrayList.get(pos).bname);

                                startActivity(intent);
                            }
                            else if(type != null && type.equalsIgnoreCase("Service"))
                            {
                                Intent intent = new Intent(Favourites.this, serviceDetails.class);
                                intent.putExtra("username", favedataArrayList.get(pos).username);
                                intent.putExtra("Name", favedataArrayList.get(pos).bname);

                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(Favourites.this, "" + type, Toast.LENGTH_SHORT).show();
                            }




                        }
                    });



        /*
            collectType.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String type = document.getString("Type");

        
                            }
                            
                    } 
                    else
                    {
                        Toast.makeText(Favourites.this, "Can't continue, sorry!", Toast.LENGTH_SHORT).show();
                    }
                }

            });

         */






        /*
        fs.collection("Petlover")
                .document(email).collection("favorites")
                .document(pos + "").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Intent intent = new Intent( Favourites.this, card_details2.class);
                        intent.putExtra("username", favedataArrayList.get(pos).username);

                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Favourites.this, "Network Err!", Toast.LENGTH_SHORT).show();
                    }
                });

         */

    }
}