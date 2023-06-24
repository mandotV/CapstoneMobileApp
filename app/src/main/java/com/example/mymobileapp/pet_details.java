package com.example.mymobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class pet_details extends AppCompatActivity {


    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    TextView pname, breed, sex, bday, age, kg, color, petprice, desc;
    String name, storeName, email, pprice, petimglink;
    Button cancel, add;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_details);

        email = firebaseUser.getEmail();
        name = getIntent().getStringExtra("Name");
        pprice = getIntent().getStringExtra("Price");
        petimglink = getIntent().getStringExtra("petimglink");
        storeName = getIntent().getStringExtra("username");

        pname = findViewById(R.id.pet_name);
        breed = findViewById(R.id.pet_breed);
        sex = findViewById(R.id.pet_sex);
        bday = findViewById(R.id.pet_bdate);
        age = findViewById(R.id.pet_age);
        kg = findViewById(R.id.pet_weight);
        desc = findViewById(R.id.pet_desc);
        color = findViewById(R.id.pet_color);
        petprice = findViewById(R.id.petprice);

        add = findViewById(R.id.pet_add);
        //cancel = findViewById(R.id.pet_cancel);

        img = findViewById(R.id.pet_pp);


        showpetdata();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReservationPet();
            }
        });
/*
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(pet_details.this, "Option Cancelled!", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(serviceDetails.this, vetclinic.class));
                finish();
            }
        });

 */



    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
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
                            .document(storeName).collection("notifications")
                            .document();

                    // Add the data to the new document
                    docPaste.set(document).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(pet_details.this, "<3", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Error occurred while retrieving the original document
                    Toast.makeText(pet_details.this, "Error Process", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void addReservationPet()
    {
        fs.collection("Petlover").document(email)
                .collection("cart").document(name).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists())
                        {
                            String name = pname.getText().toString();


                            Intent intent = new Intent(pet_details.this, reservation3.class);
                            intent.putExtra("name", name);
                            //intent.putExtra("")


                            startActivity(intent);
                        }
                        else
                        {

                            String breed2 = breed.getText().toString();
                            String pr2 = petprice.getText().toString();
                            String age2 = age.getText().toString();
                            String kg2 = kg.getText().toString();
                            String color2 = color.getText().toString();
                            String sex2 = sex.getText().toString();
                            String bday2 = bday.getText().toString();
                            String descript = desc.getText().toString();
                           // String type = "Pet";



                            HashMap hashMap = new HashMap();
                            hashMap.put("email", email);
                            hashMap.put("name", name);
                            hashMap.put("breed", breed2);
                            hashMap.put("price", pr2);
                            hashMap.put("owner", storeName);
                            hashMap.put("description", descript);
                            hashMap.put("age", age2);
                            hashMap.put("weight", kg2);
                            hashMap.put("color", color2);
                            hashMap.put("sex", sex2);
                            hashMap.put("birthdate", bday2);
                            hashMap.put("cartlink", petimglink);
                            hashMap.put("Type", "Pet");



                            fs.collection("Petlover").document(email)
                                    .collection("cart").document(name).set(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            toNoticifationWeb();

                                            Toast.makeText(pet_details.this, "Added to Cart Successfully!", Toast.LENGTH_SHORT).show();
                                            //startActivity(new Intent(productsDetails.this, vetclinic.class));
                                            finish();
                                        }
                                    });
                        }
                    }
                });


    }

    private void showpetdata() {

        fs.collection("petstore").document(storeName)
                .collection("pets")
                .document(name).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String pBdate = documentSnapshot.getString("Birthdate");
                        String pAge = documentSnapshot.getString("Age");
                        String pWeight = documentSnapshot.getString("Weight");
                        String pDescription = documentSnapshot.getString("Description");
                        String pColor = documentSnapshot.getString("Color");
                        String pbreed = documentSnapshot.getString("Breed");
                        String pSex = documentSnapshot.getString("Sex");
                        //String pcat = documentSnapshot.getString("Category");
                        //String ppimage = documentSnapshot.getString("petimglink");

                        pname.setText(name);
                        bday.setText(pBdate);
                        breed.setText(pbreed);
                        sex.setText(pSex);
                        age.setText(pAge);
                        kg.setText(pWeight);
                        desc.setText(pDescription);
                        color.setText(pColor);
                        petprice.setText(pprice);
                        Glide.with(pet_details.this).load(petimglink)
                                .into(img);





                    }
                });

    }
}