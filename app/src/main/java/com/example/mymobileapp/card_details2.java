package com.example.mymobileapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymobileapp.databinding.ActivityCardDetails2Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class card_details2 extends AppCompatActivity {

    private ActivityCardDetails2Binding binding;

    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    String usernm, id, email, image, clue;
    Timestamp serverT = new Timestamp(new Date());

    /*
    RecyclerView recyclerView;
    ArrayList<serviceModel> serviceModelArrayList;
    adapterService adapterService;

     */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardDetails2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        id = firebaseUser.getEmail();
        usernm = getIntent().getStringExtra("username");

        fs.collection("vetclinic").document(usernm).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists())
                {
                    showVet();
                    clue = "vetclinic";
                    binding.Pets.setVisibility(View.INVISIBLE);
                    binding.Vets.setVisibility(View.VISIBLE);
                }
                else
                {
                    showStore();
                    clue = "petstore";
                    binding.Pets.setVisibility(View.VISIBLE);
                    binding.Vets.setVisibility(View.INVISIBLE);
                }

            }
        });

        chck();
        createNotification();
        //chckRecycler();
       // EventChangeListener();

        binding.Pets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(card_details2.this, petlist.class);
                intent.putExtra("username", usernm);
                startActivity(intent);
            }
        });

        binding.Vets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewVets();

            }
        });

        binding.addRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                addRatings();
            }
        });

        binding.toMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fs.collection("Petlover").document(id).collection("messages")
                                .document(email)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if(documentSnapshot.exists()) {

                                    String s = documentSnapshot.getId();
                                    Intent intent = new Intent(card_details2.this, chat.class);
                                    intent.putExtra("receiver", s);

                                    startActivity(intent);
                                }
                                else
                                {
                                    //Toast.makeText(card_details2.this, "" + d, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(card_details2.this, Msg.class);
                                    intent.putExtra("receiver", email);
                                    intent.putExtra("imagelink", image);

                                    startActivity(intent);
                                }

                            }
                        });



            }


        });

        binding.Revs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(clue!= null)
                {
                    if(clue == "vetclinic")
                    {
                        seeReviewsClinic();
                    }
                    else if(clue == "petstore")
                    {
                        seeReviewsStore();
                    }
                    else
                    {
                        Toast.makeText(card_details2.this, "No clue shown!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(card_details2.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        binding.toFavouriteVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fs.collection("Petlover").document(id).collection("favorites").document(usernm).get().
                        addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if(documentSnapshot.exists())
                                {
                                    binding.toFavouriteVet.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);

                                    fs.collection("Petlover").document(id).collection("favorites")
                                            .document(usernm)
                                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    Toast.makeText(card_details2.this, "Removed from favorites!", Toast.LENGTH_SHORT).show();
                                                    notificationRemove();
                                                }
                                            });

                                }
                                else
                                {
                                    binding.toFavouriteVet.setBackgroundResource(R.drawable.ic_baseline_red_24);
                                    addFave();
                                    notificationAdd();

                                }


                            }
                        });

            }
        });

        binding.toServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startActivity(new Intent(card_details2.this, vetclinic.class));

                //String s = clue;
                String u = usernm;
                String eo = email;
                Intent intent = new Intent(card_details2.this, vetclinic.class);
                intent.putExtra("clue", clue);
                intent.putExtra("username", u);
                intent.putExtra("emailOwner", eo);

                startActivity(intent);

            }
        });



    }


    public void viewVets()
    {
        //FirebaseUser fu = firebaseAuth.getCurrentUser();
        //String email = fu.getEmail();
        CollectionReference collection = fs.collection("vetclinic")
                .document(usernm).collection("vets");

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Available Veterinarian");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.veterrecycle, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_vet);




        //DocumentAdapter adapter = new DocumentAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        collection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        // get the list of documents from the query snapshot
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        // create a new DocumentAdapter with the documents list
                        adapterVeter adapter = new adapterVeter(documents, new adapterVeter.OnItemClickListener() {
                            @Override
                            public void onItemClick(DocumentSnapshot documentSnapshot) {

                                String veterName = documentSnapshot.getString("Name");
                                String veterEmail = documentSnapshot.getString("Email");
                                String veterNumber = documentSnapshot.getString("Number");
                                String veterImg = documentSnapshot.getString("vetimglink");

                                //Toast.makeText(card_details2.this, "you clicked!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(card_details2.this, vetlist.class);
                                intent.putExtra("username", usernm);
                                intent.putExtra("name", veterName);
                                intent.putExtra("phone", veterNumber);
                                intent.putExtra("email", veterEmail);
                                intent.putExtra("image", veterImg);
                                startActivity(intent);


                            }
                        }, card_details2.this);

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

    public void seeReviewsClinic()
    {
        CollectionReference collection = fs.collection("vetclinic")
                .document(usernm).collection("ratings");

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Customer's feedback");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ratingandreviewsrecycle, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.ratingsandreviewsrecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        collection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        // get the list of documents from the query snapshot
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        // create a new DocumentAdapter with the documents list
                        adapterReviews adapter = new adapterReviews(documents);

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


    public void seeReviewsStore()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Customer's feedback");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ratingandreviewsrecycle, null);

        CollectionReference collection = fs.collection("petstore")
                .document(usernm).collection("ratings");
        // String starString;
        //int starInt;

        //DocumentReference docRef = fs.collection("petstore").document(usernm)
        //         .collection("ratings").document();
        /*
        collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    for (DocumentSnapshot document : documents) {
                        // Do something with the document

                        starString = document.getString("rates");
                        double value = Double.parseDouble(starString);
                        starInt = (int) Math.round(value);
                    }
                } else {
                    // There was an error getting the documents
                    Toast.makeText(card_details2.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

         */

        RecyclerView recyclerView = dialogView.findViewById(R.id.ratingsandreviewsrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        collection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        //for (DocumentSnapshot document : documents)

                        // get the list of documents from the query snapshot
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        /*
                        if(documents.isEmpty())
                        {
                            Toast.makeText(card_details2.this, "No reviews!", Toast.LENGTH_SHORT).show();
                        }

                         */

                        // create a new DocumentAdapter with the documents list
                        adapterReviews adapterRev = new adapterReviews (documents);

                        // set the adapter of the RecyclerView
                        recyclerView.setAdapter(adapterRev);
                    }
                });

        builder.setNegativeButton("back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setView(dialogView);


        AlertDialog dialog = builder.create();
        dialog.show();



    }



    public void showVet(){

        fs.collection("vetclinic").document(usernm)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                    String name = documentSnapshot.getString("brandname");
                    String address = documentSnapshot.getString("address");
                    image = documentSnapshot.getString("imagelink");
                    email = documentSnapshot.getString("email");

                    binding.detailEmail.setText(email);
                    binding.detailName.setText(name);
                    binding.detailAddress.setText(address);
                    Glide.with(card_details2.this).load(image)
                            .override(400, 500)
                            .into(binding.detailImage);



            }
        });

        fs.collection("vetclinic").document(usernm)
                .collection("services").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            // Check if the collection is empty by calling isEmpty()
                            if (snapshot.isEmpty()) {
                                Toast.makeText(card_details2.this, "This clinic doesn't have services as of now!", Toast.LENGTH_SHORT).show();
                            } else {

                            }
                        } else {
                            Toast.makeText(card_details2.this, "Something's wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        /*
        fs.collection("ratings").document(usernm).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String rts = documentSnapshot.getString("rates");
                        binding.ratings.setText(rts);
                    }
                });

         */
        List<Double> db = new ArrayList<>();


        fs.collection("vetclinic").document(usernm).collection("ratings")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot docs : task.getResult())
                            {

                                double v = Double.parseDouble(docs.getString("rates"));
                                db.add(v);
                                int numOfRates = db.size();
                                double total = db.stream().mapToDouble(f -> f.doubleValue()).sum();
                                double avg = total/numOfRates;
                                String dec = new DecimalFormat("#.#").format(avg);
                                binding.ratings.setText(dec);

                            }

                        }
                    }
                });



    }

    public void showStore(){

        fs.collection("petstore").document(usernm)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String name = documentSnapshot.getString("brandname");
                        String address = documentSnapshot.getString("address");
                        image = documentSnapshot.getString("imagelink");
                        email = documentSnapshot.getString("email");

                        binding.detailEmail.setText(email);
                        binding.detailName.setText(name);
                        binding.detailAddress.setText(address);
                        Glide.with(card_details2.this).load(image)
                                .override(400, 500)
                                .into(binding.detailImage);


                    }
                });

        fs.collection("petstore").document(usernm)
                .collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            // Check if the collection is empty by calling isEmpty()
                            if (snapshot.isEmpty()) {
                                Toast.makeText(card_details2.this, "This store doesn't have products as of now!", Toast.LENGTH_SHORT).show();
                            } else {

                            }
                        } else {
                            Toast.makeText(card_details2.this, "Something's wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    List<Double> db = new ArrayList<>();


        fs.collection("petstore").document(usernm).collection("ratings")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot docs : task.getResult())
                            {

                                double v = Double.parseDouble(docs.getString("rates"));
                                db.add(v);
                                int numOfRates = db.size();
                                double total = db.stream().mapToDouble(f -> f.doubleValue()).sum();
                                double avg = total/numOfRates;
                                String dec = new DecimalFormat("#.#").format(avg);
                                binding.ratings.setText(dec);

                            }

                        }
                    }
                });



    }
    public void notificationAdd()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Favorites")
                .setSmallIcon(R.drawable.logo)
                .setShowWhen(true)
                .setContentTitle("Added favorites")
                .setContentText(usernm + " successfully added to favorites")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup("Favorites Group");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(card_details2.this);
        managerCompat.notify(2, builder.build());
    }
    public void notificationRemove()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Favorites")
                .setSmallIcon(R.drawable.logo)
                .setShowWhen(true)
                .setContentTitle("Removed favorites")
                .setContentText(usernm + " successfully removed from favorites")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup("Favorites Group");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(card_details2.this);
        managerCompat.notify(3, builder.build());
    }
    public void createNotification()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("Favorites", "Favorites", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channel2 = new NotificationChannel("RFavorites", "RFavorites", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            manager.createNotificationChannel(channel2);
        }

    }

    public void addFave(){

        String bname = binding.detailName.getText().toString();
        String address = binding.detailAddress.getText().toString();


        HashMap hashMap = new HashMap();
        hashMap.put("email", email);
        hashMap.put("petlover", id);
        hashMap.put("bname", bname);
        hashMap.put("address", address);
        hashMap.put("imglink", image);
        hashMap.put("username", usernm);
        hashMap.put("Type", "Business");


        fs.collection("Petlover").document(id)
                .collection("favorites")
                .document(usernm).set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Toast.makeText(card_details2.this, "Added to Favorites!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void addRatings(){

        AlertDialog.Builder build = new AlertDialog.Builder(card_details2.this);
        LayoutInflater inflate = card_details2.this.getLayoutInflater();
        View v = inflate.inflate(R.layout.ratingfile, null);

        RatingBar rates = v.findViewById(R.id.rates);
        EditText rev = v.findViewById(R.id.edt_reviews);

        build.setView(v).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                String rrev = rev.getText().toString();
                String nowRating = String.valueOf(rates.getRating());
                /*
                Long tm = System.currentTimeMillis();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
                Date date = new Date(tm);
                String tm2 = simpleDateFormat.format(date);

                 */

                /*

                                    HashMap hash2 = new HashMap();
                                    hash2.put("id", email);
                                    hash2.put("outletType", outlet);
                                    hash2.put("message", msg);
                                    hash2.put("receiver", receiver);

                                    fs.collection("Petlover").document( + "").set(hash2)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                        }
                                                    });

                 */


                HashMap hash = new HashMap();
                //hash.put("rates", r);
                hash.put("reviews", rrev);
                hash.put("rates",nowRating);
                hash.put("sender", id);
                hash.put("receiver", email);
                hash.put("timestamp", serverT);

                if(clue.equalsIgnoreCase("vetclinic"))
                {
                    fs.collection("vetclinic").document(usernm+ "")
                            .collection("ratings").document()
                            .set(hash)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(card_details2.this, "Your Rating: " + nowRating, Toast.LENGTH_SHORT).show();

                                }
                            });
                }
                else
                {
                    fs.collection("petstore").document(usernm+ "")
                            .collection("ratings").document()
                            .set(hash)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(card_details2.this, "Your Rating: " + nowRating, Toast.LENGTH_SHORT).show();

                                }
                            });
                }





            }
        }).setNegativeButton("back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });






        AlertDialog alertDialog = build.create();
        alertDialog.show();
    }

    public void chck(){

        fs.collection("Petlover").document(id)
                .collection("favorites")
                .document(usernm).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists()){

                            binding.toFavouriteVet.setBackgroundResource(R.drawable.ic_baseline_red_24);

                        }
                        else
                        {
                            binding.toFavouriteVet.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);

                        }


                    }
                });

    }

    /*
    public void chckRecycler(){

        //recyclerView = findViewById(R.id.serviceList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        serviceModelArrayList = new ArrayList<serviceModel>();
        adapterService = new adapterService(card_details2.this, serviceModelArrayList, this);

        recyclerView.setAdapter(adapterService);


    }

    private void EventChangeListener() {

        fs.collection("vetclinic").document(usernm + "").collection("services")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error !=null){
                            Log.e("Error",error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()){


                            if(dc.getType() == DocumentChange.Type.ADDED){
                                serviceModelArrayList.add(dc.getDocument().toObject(serviceModel.class));
                            }

                            adapterService.notifyDataSetChanged();


                        }

                    }
                });
    }

    @Override
    public void onItemClick(int pos) {

        Toast.makeText(this, "!", Toast.LENGTH_SHORT).show();

        fs.collection("vetclinic").document(usernm + "")
                .collection("services").document(pos + "").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                        Intent intent = new Intent(card_details2.this, serviceDetails.class);
                        intent.putExtra("Name", serviceModelArrayList.get(pos).Name);
                        intent.putExtra("Price", serviceModelArrayList.get(pos).Price);
                        intent.putExtra("username", usernm);

                        startActivity(intent);

                    }
                });
    }

     */
}