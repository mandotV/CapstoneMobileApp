package com.example.mymobileapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class mycart extends AppCompatActivity implements interfacecart{


    RecyclerView recyclerView;
    ArrayList<cartData> cartDataArrayList;
    adapterCart adapterCart;

    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    String email = firebaseUser.getEmail();
    String dec;

    TextView totalC;
    //Button payCart;
    ImageView crtback, crthistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycart);

        totalC = findViewById(R.id.cartTotal);

        recyclerView = findViewById(R.id.recycler_cart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartDataArrayList = new ArrayList<cartData>();
        adapterCart = new adapterCart(mycart.this, cartDataArrayList, this);

        recyclerView.setAdapter(adapterCart);
       // payCart = findViewById(R.id.payCart);
        crtback = findViewById(R.id.crt_back);
        crthistory = findViewById(R.id.crt_history);

        /*
        payCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // FieldValue


                Intent intent = new Intent(mycart.this, rayzorPayEntry.class);
                intent.putExtra("ammount", dec);


                startActivity(intent);
            }
        });

         */
        crtback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mycart.this, Home.class));
            }
        });

        crthistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartHisto();
            }
        });

        showCart();
        showTotal();
    }

    public void cartHisto()
    {
        //Toast.makeText(this, "Currently under construction!", Toast.LENGTH_SHORT).show();

        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = fs.collection("Petlover")
                .document(email).collection("cart-history");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cart History");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.carthistoryrecycle, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.cartHistoryRecycler);


        //DocumentAdapter adapter = new DocumentAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        collection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        // get the list of documents from the query snapshot
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        // create a new DocumentAdapter with the documents list
                        DocumentAdapter adapter = new DocumentAdapter(documents, mycart.this);

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
    public void showTotal()
    {
        List<Double> db = new ArrayList<>();


        fs.collection("Petlover").document(email).collection("cart")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot docs : task.getResult())
                            {

                                double v = Double.parseDouble(docs.getString("price"));
                                db.add(v);
                                double total = db.stream().mapToDouble(f -> f.doubleValue()).sum();
                                dec = new DecimalFormat(".##").format(total);
                                //am = Integer.parseInt(String.valueOf(total));
                                totalC.setText(dec);

                            }

                        }
                    }
                });
    }

    public void showCart(){

        fs.collection("Petlover").document(email).collection("cart")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.e("Error", error.getMessage());
                            return;
                        }
                        if(value == null)
                        {
                            Toast.makeText(mycart.this, "No data!", Toast.LENGTH_SHORT).show();
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {

                            if(dc.getType() == DocumentChange.Type.REMOVED)
                            {
                                int index = dc.getOldIndex();
                                cartDataArrayList.remove(index);
                                adapterCart.notifyItemRemoved(index);
                            }

                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                cartDataArrayList.add(dc.getDocument().toObject(cartData.class));
                            }

                            adapterCart.notifyDataSetChanged();
                            //adapterCart.notif



                        }

                    }
                });
    }

    @Override
    public void onItemClick(int pos) {


        Toast.makeText(this, "Product #" + pos, Toast.LENGTH_SHORT).show();


        /*
        String id = fs.collection("Petlover").document(email)
                .collection("cart").document().getId();
         */


        fs.collection("Petlover").document(email)
                    .collection("cart").document("" + pos)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            String type = cartDataArrayList.get(pos).Type;


                            //String id = documentSnapshot.getId();
                            //Intent intent = new Intent(mycart.this, reservation.class);
                            //intent.putExtra("name", cartDataArrayList.get(pos).name);
                            //intent.putExtra("id", id);
                            //startActivity(intent);

                            if(type.equalsIgnoreCase("Product"))
                            {
                                Intent intent = new Intent(mycart.this, reservation.class);
                                intent.putExtra("name", cartDataArrayList.get(pos).name);
                                startActivity(intent);
                            }
                            else
                            {
                                Intent intent = new Intent(mycart.this, reservation3.class);
                                intent.putExtra("name", cartDataArrayList.get(pos).name);
                                startActivity(intent);
                            }
                        }
                    });

            /*
            fs.collection("Petlover").document(email)
                    .collection("cart").document("" + pos)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            //String id = documentSnapshot.getId();

                            Intent intent = new Intent(mycart.this, reservation3.class);
                            intent.putExtra("name", cartDataArrayList.get(pos).name);
                            //intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    });

             */




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mycart.this, Home.class));
    }
}