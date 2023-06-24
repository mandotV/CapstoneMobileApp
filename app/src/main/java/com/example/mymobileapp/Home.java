package com.example.mymobileapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymobileapp.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity{

    private ActivityHomeBinding binding;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    ProgressDialog pd;
    //String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        firebaseAuth = FirebaseAuth.getInstance();

        //progressLogin();
        checkUser();
        checkSelf();



        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startActivity(new Intent(Home.this, Login.class));
                dialogLogout();

            }
        });
        binding.imgMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Home.this, Msg.class));

            }
        });

        binding.cardReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(Home.this, myappointment.class)));
            }
        });

        binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, profile.class));

            }
        });
        binding.cardFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Favourites.class));
            }
        });
        binding.cardNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, mycart.class));

            }
        });

        binding.buttonReceipts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkReceipt();
            }
        });




        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.action_home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.action_map:
                        progressToMap();
                        startActivity(new Intent(getApplicationContext(), mapMod.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }

    /*
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        // Handle the item click here

    }

     */


    public void checkReceipt()
    {
        FirebaseUser fu = firebaseAuth.getCurrentUser();
        String email = fu.getEmail();
        CollectionReference collection = fs.collection("Petlover")
                .document(email).collection("payment receipts");

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Payment Receipts");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.receiptsrecycler, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.receiptRecycler);




        //DocumentAdapter adapter = new DocumentAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        collection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        // get the list of documents from the query snapshot
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        // create a new DocumentAdapter with the documents list
                        receiptAdapter adapter = new receiptAdapter(documents, new receiptAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(DocumentSnapshot documentSnapshot) {


                                Intent intent = new Intent(Home.this, receiptDetails.class);
                                intent.putExtra("payment", documentSnapshot.getString("payment"));
                                intent.putExtra("amount", documentSnapshot.getString("amount"));
                                intent.putExtra("name", documentSnapshot.getString("name"));
                                intent.putExtra("quantity", documentSnapshot.getString("quantity"));
                                intent.putExtra("type", documentSnapshot.getString("type"));
                                intent.putExtra("pickup date", documentSnapshot.getString("reservation date"));
                                intent.putExtra("schedule", documentSnapshot.getString("schedule"));
                                intent.putExtra("owner", documentSnapshot.getString("owner"));
                                intent.putExtra("btype", documentSnapshot.getString("business Type"));
                                intent.putExtra("docId", documentSnapshot.getId());
                               //intent.putExtra("category", documentSnapshot.getString("category"));


                                startActivity(intent);
                            }
                        });

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

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogLogout()
    {
            AlertDialog.Builder build = new AlertDialog.Builder(Home.this);
            build.setMessage("Are you sure you want to logout?");
            //LayoutInflater inflate = Home.this.getLayoutInflater();

            //View v = inflate.inflate(R.layout.deleteitem, null);

            build.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    firebaseAuth.signOut();
                    checkUser();

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Toast.makeText(Home.this, "Action cancelled!", Toast.LENGTH_SHORT).show();
                    //finish();
                    dialogInterface.dismiss();
                }
            });

            AlertDialog alertDialog = build.create();
            build.show();





    }

    public void progressLogout()
    {
        pd = new ProgressDialog(this);
        pd.setMessage("Logging out...:( </3");
        pd.setCancelable(false);

        // Show the progress dialog
        pd.show();

        // Perform the login process in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Simulate a long running task
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Dismiss the progress dialog
                pd.dismiss();
            }
        }).start();
    }

    public void progressToMap()
    {
        pd = new ProgressDialog(this);
        pd.setMessage("Opening locator.....</>");
        pd.setCancelable(false);

        // Show the progress dialog
        pd.show();

        // Perform the login process in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Simulate a long running task
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Dismiss the progress dialog
                pd.dismiss();
            }
        }).start();
    }




    private void checkUser(){

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        //String email = firebaseUser.getEmail();

        /*

        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            List<String> signInMethods = result.getSignInMethods();
                            if (signInMethods.isEmpty()) {
                                // Email does not exist in authentication system
                                Toast.makeText(Home.this, "No email is registered!", Toast.LENGTH_SHORT).show();

                                //finish();

                            } else {
                                // Email exists in authentication system

                                String email = firebaseUser.getEmail();

                                DocumentReference doc = fs.collection("Petlover").document(email);
                                DocumentReference doc2 = fs.collection("profilePic").document(email);


                                doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        String fullName = documentSnapshot.getString("Name");

                                        if(email != null)
                                        {

                                            binding.homeEmail.setText(email);

                                            if(fullName != null)
                                            {
                                                binding.homeEmail.setText(fullName);
                                            }
                                            else
                                            {
                                                binding.homeEmail.setText(email);
                                            }

                                        }


                                    }
                                });


                                doc2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                String pp = documentSnapshot.getString("ppUrl");
                                                if(pp != null) {
                                                    Glide.with(Home.this).load(pp)
                                                            .override(400, 500)
                                                            .into(binding.imgProfile);
                                                }
                                                else
                                                {
                                                    binding.imgProfile.setImageResource(R.drawable.user);
                                                }

                                            }
                                        });



                            }
                        } else {
                            // An error occurred while trying to check the email
                            Toast.makeText(Home.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

         */


        if(firebaseUser == null){

            startActivity(new Intent(this, Login.class));
            progressLogout();
            //finish();

        }
        else {
            String email = firebaseUser.getEmail();

            DocumentReference doc = fs.collection("Petlover").document(email);
            //DocumentReference doc2 = fs.collection("profilePic").document(email);

            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    String fullName = documentSnapshot.getString("Name");


                            if (email != null) {

                                binding.homeEmail.setText(email);

                                if (fullName == null)
                                {
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("Email", email);
                                    hashMap.put("Name", "");
                                    hashMap.put("Address", "");
                                    hashMap.put("Age", "");
                                    hashMap.put("Mobile", "");
                                    hashMap.put("Birthdate", "");
                                    hashMap.put("ppUrl", "");

                                    doc.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Toast.makeText(Home.this, "Welcome <3", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                                else if(fullName.equalsIgnoreCase(""))
                                {
                                    binding.homeEmail.setText(email);
                                }
                                else
                                {
                                    binding.homeEmail.setText(fullName);
                                }

                            }


                }
            });



            fs.collection("Petlover").document(email)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            String pp = documentSnapshot.getString("ppUrl");

                            if (pp != null) {

                                Glide.with(Home.this).load(pp)
                                        .override(400, 500)
                                        .into(binding.imgProfile);

                            }
                            else {
                                binding.imgProfile.setImageResource(R.drawable.user);
                            }

                        }
                    });


        }







    }


    public void checkSelf()
    {

        if (ContextCompat.checkSelfPermission(Home.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                //turnOnLocation();
            } else
            {
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                //reqCode = 1;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(Home.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        //turnOnLocation();

                    }
                } else {
                    //reqCode = 2;
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}