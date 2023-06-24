package com.example.mymobileapp;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class serviceDetails extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    TextView cat, desc, pr, nm, vet, addDateText;
    ImageView img;
    Button cancel, add, faveServ;
    ImageButton addPicker;
    String name, price, usernm, email, clue, containerImg;
    //Spinner spinner, DayOfWeek;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    List<String> dayOfWeek = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        name = getIntent().getStringExtra("Name");
        //price = getIntent().getStringExtra("Price");
        usernm = getIntent().getStringExtra("username");
        clue = getIntent().getStringExtra("clue");

        email = firebaseUser.getEmail();

        cat = findViewById(R.id.service_cat);
        desc = findViewById(R.id.service_desc);
        pr = findViewById(R.id.service_Price);
        nm = findViewById(R.id.service_name);
        vet = findViewById(R.id.service_Vet);
        addPicker = findViewById(R.id.addPicker);
        addDateText = findViewById(R.id.addDateText);
        //addedDate = findViewById(R.id.addedDate);
        //dateDialog = findViewById(R.id.setDateCompare);
        //spinner = findViewById(R.id.addDate);
        //DayOfWeek = findViewById(R.id.addDayOfWeek);

        img = findViewById(R.id.service_image);

        //cancel = findViewById(R.id.btn_cancel);
        add = findViewById(R.id.btn_appoint);
        faveServ = findViewById(R.id.to_FavoriteServ);

        showData();
        createNotification();
        removeFave(); // need further checking
        checkVetStatus();
        //adapterr();
        //adapterDay();
        chck();
        add.setEnabled(false);

        /*
        dateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datepicker = new datepicker();
                datepicker.show(getSupportFragmentManager(), "Set Pick-Up date");
            }
        });
         */

        addPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datepicker = new datepicker();
                datepicker.show(getSupportFragmentManager(), "Set Pick-Up date");
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String vett = vet.getText().toString();
                fs.collection("Petlover").document(email)
                                .collection("appointments").document(name)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                
                                if(documentSnapshot.exists())
                                {
                                    // to appointment details
                                    Toast.makeText(serviceDetails.this, "Appointment already booked", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    addReservation();
                                    notification();
                                }
                            }
                        });
                

            }
        });
        /*

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(serviceDetails.this, "Option Cancelled!", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(serviceDetails.this, vetclinic.class));
                finish();
            }
        });

         */

        faveServ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference docfs = fs.collection("Petlover").document(email)
                        .collection("favorites").document(name);

                docfs.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists())
                        {
                            faveServ.setBackgroundResource(R.drawable.bookmark_selector);

                            docfs.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(serviceDetails.this, "Service removed from favorites!", Toast.LENGTH_SHORT).show();
                                    notificationServRemove();
                                }
                            });
                        }
                        else
                        {
                            faveServ.setBackgroundResource(R.drawable.ic_baseline_bookmark_24);
                            //addProdFave();

                            DocumentReference retOwner = fs.collection("vetclinic")
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
                                    hashMap.put("imglink", containerImg);
                                    hashMap.put("username", usernm);
                                    hashMap.put("Type", "Service");

                                    docfs.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Toast.makeText(serviceDetails.this, "Service added to Favorites!", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                    notificationServAdd();

                                }
                            });

                        }
                    }
                });
            }
        });




    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        Calendar currentDate = Calendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String selectedCartDate = dateFormat.format(c.getTime());
        //String currentCartDate = dateFormat.format(currentDate.getTime());

        if(c.before(currentDate))
        {
            Toast.makeText(this, "Date has already passed!", Toast.LENGTH_SHORT).show();
            add.setEnabled(false);
        }
        else
        {
            addDateText.setText(selectedCartDate);
            add.setEnabled(true);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    public void removeFave()
    {
        DocumentReference checkIfExist = fs.collection("vetclinic").document(usernm)
                .collection("services").document(name);

        checkIfExist.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists())
                {

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
                                faveServ.setBackgroundResource(R.drawable.bookmark_selector);

                                docfs.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(serviceDetails.this, "Service nowhere to be found!", Toast.LENGTH_SHORT).show();
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


    public void notificationServAdd()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Favorites")
                .setSmallIcon(R.drawable.logo)
                .setShowWhen(true)
                .setContentTitle("Added favorites")
                .setContentText(name + " successfully added to favorites")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup("Favorites Group");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(serviceDetails.this);
        managerCompat.notify(3, builder.build());
    }

    public void notificationServRemove()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Favorites")
                .setSmallIcon(R.drawable.logo)
                .setShowWhen(true)
                .setContentTitle("Removed favorites")
                .setContentText(name + " successfully removed from favorites")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup("Favorites Group");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(serviceDetails.this);
        managerCompat.notify(2, builder.build());

    }
    public void showData(){

        fs.collection("vetclinic").document(usernm +"")
                .collection("services")
                .document(name + "")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String categ = documentSnapshot.getString("Name");
                        String descrip = documentSnapshot.getString("Description");
                        String veter = documentSnapshot.getString("Vet");
                        containerImg = documentSnapshot.getString("servimglink");
                        price = documentSnapshot.getString("Price");


                        //addedDate.setText(Date);
                        nm.setText(name);
                        pr.setText(price);
                        desc.setText(descrip);
                        cat.setText(categ);
                        vet.setText(veter);

                        if(containerImg != null) {

                            Glide.with(serviceDetails.this).load(containerImg)
                                    .override(400,500)
                                    .into(img);
                        }
                        else
                        {
                            img.setImageResource(R.drawable.appointment);
                        }


                    }
                });
    }

    public void notification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Appointment")
                .setSmallIcon(R.drawable.logo)
                .setShowWhen(true)
                .setContentTitle("Appointment")
                .setContentText(name + " successfully booked! You can cancel the appointment 1 minute from now")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(serviceDetails.this);
        managerCompat.notify(1, builder.build());
    }

    public void createNotification()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("Appointment", "Appointment", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }



    public void chck(){

        fs.collection("Petlover").document(email)
                .collection("favorites")
                .document(name).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists()){

                            faveServ.setBackgroundResource(R.drawable.ic_baseline_bookmark_24);

                        }
                        else
                        {
                            faveServ.setBackgroundResource(R.drawable.ic_baseline_bookmark_border_24);

                        }


                    }
                });

    }

    public void checkVetStatus()
    {


        fs.collection("vetclinic").document(usernm +"")
                .collection("services")
                .document(name + "")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                       String vetName = documentSnapshot.getString("Vet");


                                DocumentReference docCheckStatus = fs.collection("vetclinic").document(usernm)
                                        .collection("vets").document(vetName);

                                docCheckStatus.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        String vetStatus = documentSnapshot.getString("status");


                                        if(vetStatus.equalsIgnoreCase("Busy"))
                                        {
                                            Toast.makeText(serviceDetails.this, "Veterinarian is fully booked!", Toast.LENGTH_SHORT).show();
                                            add.setEnabled(false);
                                        }
                                        else
                                        {
                                            add.setEnabled(true);
                                        }

                                    }
                                });
                    }
                });
    }

    public void toNoticifationWeb()
    {
        DocumentReference docCopyToNotification = fs.collection("Petlover").document(email)
                .collection("appointments")
                .document(name);


        docCopyToNotification.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    // Get the document data as a Map object
                    Map<String, Object> document = task.getResult().getData();

                    // Create a new document in Firestore
                    DocumentReference docPaste = fs.collection("vetclinic")
                            .document(usernm).collection("notifications")
                            .document();

                    // Add the data to the new document
                    docPaste.set(document).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(serviceDetails.this, "<3", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Error occurred while retrieving the original document
                    Toast.makeText(serviceDetails.this, "Error Process", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void addReservation(){

/*

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
                            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm");




                           // Calendar cale = Calendar.getInstance();

                           // cale.setTime(date);//

                            cal.add(Calendar.MINUTE, 1);

                            int hour = cal.get(Calendar.HOUR_OF_DAY);
                            Date newDate = cal.getTime();


                            String timestamp2 = simpleDateFormat.format(newDate);
                            String timestamp3 = simpleDateFormat2.format(date);

                            String timed;

                            if(hour >=6 && hour <18)
                            {
                                timed = "day";
                            }
                            else
                            {
                                timed = "night";
                            }

 */


                            //SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm");
                            //String timestamp3 = simpleDateFormat2.format(date);
                            //Calendar calendar = Calendar.getInstance();
                            //calendar.setTimeInMillis(timestamp);
                            //int minute = calendar.get(Calendar.MINUTE);

                            long timestamp = System.currentTimeMillis();
                            Date date = new Date(timestamp);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);





                            SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("MM-dd-yyyy");
                            String timestamp4 = simpleDateFormat3.format(date);

                            String cat2 = cat.getText().toString();
                            String descript = desc.getText().toString();
                            String pr2 = pr.getText().toString();
                            String vet2 = vet.getText().toString();
                            //String dateSpin = spinner.getSelectedItem().toString();
                            String dateSet = addDateText.getText().toString();

                            /*
                            Calendar expcal = Calendar.getInstance();

                            try {
                                        Date expdate = simpleDateFormat3.parse(dateSet);
                                        expcal.add(Calendar.DATE, 3);
                                        expcal.setTime(expdate);

                            } catch (ParseException e) {
                                        e.printStackTrace();
                            }

                             */


                            if(dateSet.equalsIgnoreCase("Date"))
                            {
                                dateSet = timestamp4;
                                add.setEnabled(false);
                            }
                            String outlet = "Vet Clinic";
                            String type = "Service";

                            //Calendar cal = Calendar.getInstance();
                            //cal.set(Calendar.MINUTE, 5);
                            //String date = addedDate.getText().toString();



                            HashMap hashMap = new HashMap();
                            //hashMap.put("currentTime", timestamp3);
                            //hashMap.put("time", timed);
                            //hashMap.put("expiration", timestamp2);
                            //hashMap.put("timed", timed);
                            hashMap.put("email", email);
                            hashMap.put("name", cat2);
                            hashMap.put("price", pr2);
                            hashMap.put("description", descript);
                            hashMap.put("vet", vet2);
                            hashMap.put("owner", usernm);
                            hashMap.put("business Type", outlet);
                            hashMap.put("servimglink", containerImg);
                            hashMap.put("date", dateSet);
                            hashMap.put("Type", type);

                                fs.collection("Petlover").document(email)
                                        .collection("appointments").document(cat2).set(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                toNoticifationWeb();
                                                Toast.makeText(serviceDetails.this, "Reservation Successful!", Toast.LENGTH_SHORT).show();
                                                //startActivity(new Intent(serviceDetails.this, vetclinic.class));
                                                finish();
                                            }
                                        });

                   }

}