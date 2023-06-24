package com.example.mymobileapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class productsDetails extends AppCompatActivity {


    TextView cat, desc, pr, nm, stocks;
    EditText quantiti;
    ImageView img;
    Button cancel, add, decre, incre, faveProd;
    String name, price, usernm, email, date, clue, image, quantity;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    int quant = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);

        name = getIntent().getStringExtra("Name");
        //price = getIntent().getStringExtra("Price");
        usernm = getIntent().getStringExtra("username");
        //date = getIntent().getStringExtra("date");
        //clue = getIntent().getStringExtra("clue");

        email = firebaseUser.getEmail();

        cat = findViewById(R.id.product_cat);
        desc = findViewById(R.id.product_desc);
        pr = findViewById(R.id.product_Price);
        nm = findViewById(R.id.product_name);
        //addedDate = findViewById(R.id.addedDate);
        //addDate = findViewById(R.id.addDate);
        quantiti = findViewById(R.id.quantiti);
        decre = findViewById(R.id.decre);
        incre = findViewById(R.id.incre);
        stocks = findViewById(R.id.product_Stock);

        img = findViewById(R.id.product_image);

       // cancel = findViewById(R.id.btn_cancel);
        add = findViewById(R.id.btn_cart);
        faveProd = findViewById(R.id.to_FavouriteProd);

        showData();
        createNotification();
        chck();

        removeFave(); //need to check further


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addReservationStore();
                notification();

            }
        });

        /*

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(productsDetails.this, "Option Cancelled!", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(serviceDetails.this, vetclinic.class));
                finish();
            }
        });

         */

        faveProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference docfs = fs.collection("Petlover").document(email)
                        .collection("favorites").document(name);

               // DocumentReference docfs = fs.collection("petstore").document(email)
                       // .collection("favorites").document(name);

                docfs.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists())
                        {
                            faveProd.setBackgroundResource(R.drawable.bookmark_selector);

                            docfs.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Toast.makeText(productsDetails.this, "Product removed from favorites!", Toast.LENGTH_SHORT).show();
                                            notificationProdRemove();
                                        }
                                    });
                        }
                        else
                        {
                            faveProd.setBackgroundResource(R.drawable.ic_baseline_bookmark_24);
                            //addProdFave();

                            DocumentReference retOwner = fs.collection("petstore")
                                    .document(usernm);
                            retOwner.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    String owner = documentSnapshot.getString("email");
                                    String addrs = documentSnapshot.getString("address");

                                    HashMap hashMap = new HashMap();
                                    hashMap.put("email", owner);
                                    hashMap.put("petlover", email);
                                    hashMap.put("bname", name);
                                    hashMap.put("address", addrs);
                                    hashMap.put("imglink", image);
                                    hashMap.put("username", usernm);
                                    hashMap.put("Type", "Product");

                                    docfs.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Toast.makeText(productsDetails.this, "Product added to Favorites!", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                    notificationProdAdd();

                                }
                            });

                        }
                    }
                });

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    public void removeFave()
    {
        DocumentReference checkIfExist = fs.collection("petstore").document(usernm)
                .collection("products").document(name);

        checkIfExist.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists())
                {
                    Toast.makeText(productsDetails.this, "Product exists!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DocumentReference docfs = fs.collection("Petlover").document(email)
                            .collection("favorites").document(name);

                    docfs.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if(documentSnapshot.exists())
                            {
                                faveProd.setBackgroundResource(R.drawable.bookmark_selector);

                                docfs.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(productsDetails.this, "Product nowhere to be found!", Toast.LENGTH_SHORT).show();
                                        //notificationProdRemove();
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    public void notificationProdAdd()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Favorites")
                .setSmallIcon(R.drawable.logo)
                .setShowWhen(true)
                .setContentTitle("Added favorites")
                .setContentText(name + " successfully added to favorites")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup("Favorites Group");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(productsDetails.this);
        managerCompat.notify(3, builder.build());
    }

    public void notificationProdRemove()
    {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Favorites")
                    .setSmallIcon(R.drawable.logo)
                    .setShowWhen(true)
                    .setContentTitle("Removed favorites")
                    .setContentText(name + " successfully removed from favorites")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setGroup("Favorites Group");

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(productsDetails.this);
            managerCompat.notify(2, builder.build());

    }


    public void decrement(View view) {
        if (quant>0) {
            quant = quant - 1;
            display(quant);
        }
    }

    public void increment(View view) {

        quant = quant + 1;
        display(quant);
    }
    public void display(int num){

        quantiti.setText("" + num);
    }

    public void showData(){

        fs.collection("petstore").document(usernm +"")
                .collection("products")
                .document(name + "")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String categ = documentSnapshot.getString("Category");
                        String descrip = documentSnapshot.getString("Description");
                        price = documentSnapshot.getString("Price");
                        image = documentSnapshot.getString("prodimglink");
                        String stockz = documentSnapshot.getString("Stock");


//                      addedDate.setText(date);
                        nm.setText(name);
                        pr.setText(price);
                        desc.setText(descrip);
                        cat.setText(categ);
                        stocks.setText(stockz);
                        if(image != null) {
                            Glide.with(productsDetails.this).load(image)
                                    .override(400,500)
                                    .into(img);
                        }
                        else
                        {
                            img.setImageResource(R.drawable.product);
                        }




                    }
                });
    }

    public void notification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Pick Up")
                .setSmallIcon(R.drawable.logo)
                .setShowWhen(true)
                .setContentTitle("Pick Up")
                .setContentText(name + " successfully booked! Check your cart!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(productsDetails.this);
        managerCompat.notify(1, builder.build());
    }

    public void createNotification()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("Pick Up", "Pick Up", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }

    public void toNoticifationWeb()
    {
        DocumentReference docCopyToNotification = fs.collection("Petlover").document(email)
                .collection("cart")
                .document(name);


        docCopyToNotification.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    // Get the document data as a Map object
                    Map<String, Object> document = task.getResult().getData();

                    // Create a new document in Firestore
                    DocumentReference docPaste = fs.collection("petstore")
                            .document(usernm).collection("notifications")
                            .document();

                    // Add the data to the new document
                    docPaste.set(document).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(productsDetails.this, "<3", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Error occurred while retrieving the original document
                    Toast.makeText(productsDetails.this, "Error Process", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void addReservationStore(){

        fs.collection("Petlover").document(email)
                .collection("cart").document(name).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String qua2 = quantiti.getText().toString();
                        String qua3 = documentSnapshot.getString("quantity");
                        String name = nm.getText().toString();
                        String pr3 = pr.getText().toString();
                        String stox = stocks.getText().toString();
                        
                        if(documentSnapshot.exists())
                        {
                            if(qua2.equalsIgnoreCase(""))
                            {
                                //dec = pr2;
                                qua2 = "1";
                            }

                            int totalQua = Integer.parseInt(qua2) + Integer.parseInt(qua3);
                            
                            double sbtotal = Double.parseDouble(pr3) * Double.parseDouble(String.valueOf(totalQua));
                            String dec = new DecimalFormat(".##").format(sbtotal);

                            Map<String, Object> map = new HashMap<>();
                            map.put("quantity", String.valueOf(totalQua));
                            map.put("price", dec);
                            fs.collection("Petlover").document(email)
                                    .collection("cart").document(name).update(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {


                                            Intent intent = new Intent(productsDetails.this, reservation.class);
                                            intent.putExtra("name", name);

                                            startActivity(intent);


                                        }
                                    });

                        }
                        else
                        {

                                String cat2 = cat.getText().toString();
                                String pr2 = pr.getText().toString();
                                String outlet = "Pet Store";
                                quantity = quantiti.getText().toString();
                                String descript = desc.getText().toString();
                                String type = "Product";
                                String dec = "";

                                if(quantity.equalsIgnoreCase(""))
                                {
                                    dec = pr2;
                                    quantity = "1";
                                }
                                else
                                {
                                    double sbtotal = Double.parseDouble(pr2) * Double.parseDouble(quantity);
                                    dec = new DecimalFormat(".##").format(sbtotal);
                                }
        
        
                               // String date = addedDate.getText().toString();
        
        
                                HashMap hashMap = new HashMap();
                                hashMap.put("email", email);
                                hashMap.put("category", cat2);
                                hashMap.put("name", name);
                                hashMap.put("price", dec);
                                hashMap.put("origprice", pr2);
                                hashMap.put("owner", usernm);
                                hashMap.put("business Type", outlet);
                                hashMap.put("quantity",quantity);
                                hashMap.put("cartlink", image);
                                hashMap.put("description", descript);
                                hashMap.put("Type", type);
        
                                if(Integer.parseInt(quantity) <= Integer.parseInt(stox))
                                {
                                    
                                
        
                                fs.collection("Petlover").document(email)
                                        .collection("cart").document(name).set(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                if(Integer.parseInt(quantity) < 0 || Integer.parseInt(quantity) == 0)
                                                {
                                                    quantiti.setError("Quantity must not be below 0 or equals to zero!");
                                                }
                                                else
                                                {
                                                    toNoticifationWeb();
                                                    Toast.makeText(productsDetails.this, "Added to Cart Successful!", Toast.LENGTH_SHORT).show();
                                                    //startActivity(new Intent(productsDetails.this, vetclinic.class));
                                                    finish();
                                                }


                                            }
                                        });
                                }
                                else
                                {
                                    Toast.makeText(productsDetails.this, "Stocks is not enough to your desired quantity!", Toast.LENGTH_SHORT).show();
                                }
                                
                        
                        
                        }
                    }
                });
    }

    public void chck(){

        fs.collection("Petlover").document(email)
                .collection("favorites")
                .document(name).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists()){

                            faveProd.setBackgroundResource(R.drawable.ic_baseline_bookmark_24);

                        }
                        else
                        {
                            faveProd.setBackgroundResource(R.drawable.ic_baseline_bookmark_border_24);

                        }


                    }
                });

    }


}