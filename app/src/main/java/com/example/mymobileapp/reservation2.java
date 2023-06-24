package com.example.mymobileapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mymobileapp.databinding.ActivityReservation2Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class reservation2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivityReservation2Binding binding;

    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String email = firebaseUser.getEmail();
    String servname,price,servimglink, name, sType, branch, veter, scheduleBooked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservation2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        servname = getIntent().getStringExtra("service name");
        sType = "Service";

        checkServ();
        checkTime();
        //addTimeSchedule();
        binding.dServBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(reservation2.this, myappointment.class));
            }
        });

        binding.btnPayServEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                choosePayment();

            }
        });

        binding.btnCancelServ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delData();

            }
        });
    }
    public void delData()
    {
        AlertDialog.Builder build = new AlertDialog.Builder(reservation2.this);
        LayoutInflater inflate = reservation2.this.getLayoutInflater();
        View v = inflate.inflate(R.layout.deleteserv, null);

        build.setView(v).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                fs.collection("Petlover").document(email).collection("appointments")
                        .document(name)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(reservation2.this, "Appointment Removed!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(reservation2.this, myappointment.class));
                            }
                        });
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(reservation2.this, "Action cancelled!", Toast.LENGTH_SHORT).show();
                //finish();
                dialogInterface.dismiss();
                //onStart();
            }
        });

        AlertDialog alertDialog = build.create();
        build.show();


    }


    public void checkServ()
    {
        fs.collection("Petlover").document(email)
                .collection("appointments").document(servname)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        name = documentSnapshot.getString("name");
                        price = documentSnapshot.getString("price");
                        branch = documentSnapshot.getString("owner");
                        servimglink = documentSnapshot.getString("servimglink");
                        String descript = documentSnapshot.getString("description");
                        String date = documentSnapshot.getString("date");
                        veter = documentSnapshot.getString("vet");

                        List<String> subjects = new ArrayList<>();
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, subjects);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.addDate.setAdapter(adapter);
                        binding.addDate.setOnItemSelectedListener(reservation2.this);


                        DocumentReference docGetVet = fs.collection("vetclinic").document(branch).collection("vets")
                                .document(veter);

                        docGetVet.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if(documentSnapshot.exists())
                                {
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

                                    try {
                                        Date dateSer = dateFormat.parse(date);
                                        calendar.setTime(dateSer);

                                        String nowDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

                                        String mon = documentSnapshot.getString("Monday");
                                        String tue = documentSnapshot.getString("Tuesday");
                                        String wed = documentSnapshot.getString("Wednesday");
                                        String thur = documentSnapshot.getString("Thursday");
                                        String fri = documentSnapshot.getString("Friday");
                                        String sat = documentSnapshot.getString("Saturday");
                                        String sun = documentSnapshot.getString("Sunday");

                                        if(nowDay.equalsIgnoreCase("Monday"))
                                        {
                                            if(mon!=null && !mon.isEmpty())
                                            {
                                                subjects.add("Monday: "+ mon);
                                            }
                                            else
                                            {
                                                Toast.makeText(reservation2.this, "This vet doesn't have schedule in this day!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else if(nowDay.equalsIgnoreCase("Tuesday"))
                                        {
                                            if(tue != null && !tue.isEmpty())
                                            {
                                                subjects.add("Tuesday: " + tue);
                                            }
                                            else
                                            {
                                                Toast.makeText(reservation2.this, "This vet doesn't have schedule in this day!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else if(nowDay.equalsIgnoreCase("Wednesday"))
                                        {
                                            if(wed!=null && !wed.isEmpty())
                                            {
                                                subjects.add("Wednesday: " + wed);
                                            }
                                            else
                                            {
                                                Toast.makeText(reservation2.this, "This vet doesn't have schedule in this day!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else if(nowDay.equalsIgnoreCase("Thursday"))
                                        {
                                            if(thur!=null && !thur.isEmpty())
                                            {
                                                subjects.add("Thursday: " + thur);
                                            }
                                            else
                                            {
                                                Toast.makeText(reservation2.this, "This vet doesn't have schedule in this day!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else if(nowDay.equalsIgnoreCase("Friday"))
                                        {
                                            if(fri!=null && !fri.isEmpty())
                                            {
                                                subjects.add("Friday: " + fri);
                                            }
                                            else
                                            {
                                                Toast.makeText(reservation2.this, "This vet doesn't have schedule in this day!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else if(nowDay.equalsIgnoreCase("Saturday"))
                                        {
                                            if(sat!=null && !sat.isEmpty())
                                            {
                                                subjects.add("Saturday: "+ sat);
                                            }
                                            else
                                            {
                                                Toast.makeText(reservation2.this, "This vet doesn't have schedule in this day!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else if(nowDay.equalsIgnoreCase("Sunday"))
                                        {
                                            if(sun!=null && !sun.isEmpty())
                                            {
                                                subjects.add("Sunday: " + sun);
                                            }
                                            else
                                            {
                                                Toast.makeText(reservation2.this, "This vet doesn't have schedule in this day!", Toast.LENGTH_SHORT).show();
                                            }
                                        }


                                    } catch (ParseException e) {
                                        System.out.println("Error parsing date: " + e.getMessage());
                                    }

                                }
                                else
                                {
                                   Toast.makeText(reservation2.this, "Veterinarian does not exist!", Toast.LENGTH_SHORT).show();
                                }
                                adapter.notifyDataSetChanged();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(reservation2.this, "Error!", Toast.LENGTH_SHORT).show();
                            }
                        });


                        Glide.with(reservation2.this).load(servimglink)
                                .override(300,500)
                                .into(binding.servPp);
                        binding.descriptServ.setText(descript);
                        binding.servName.setText(name);
                        binding.servPrice.setText(price);
                        binding.servDate.setText(date);
                        binding.ownerServ.setText(branch);


                    }
                });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selected = adapterView.getItemAtPosition(i).toString();
        scheduleBooked = selected;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(this, "Please select a schedule!", Toast.LENGTH_SHORT).show();
    }

    public void checkTime()
    {

        DocumentReference checkExp = fs.collection("Petlover").document(email)
                .collection("appointments").document(servname);

        long timestamp = System.currentTimeMillis();
        Date date = new Date(timestamp);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("MM-dd-yyyy");
        String timestamp4 = simpleDateFormat3.format(date);

        checkExp.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String rsrvDate = documentSnapshot.getString("date");

                if(timestamp4.equalsIgnoreCase(rsrvDate))
                {
                    checkExp.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(reservation2.this, "Appointment already expired!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {

                    Calendar expcal = Calendar.getInstance();

                    try {
                        Date expdate = simpleDateFormat3.parse(rsrvDate);
                       // expcal.add(Calendar.DAY_OF_MONTH);
                        expcal.setTime(expdate);

                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        expcal.set(Calendar.HOUR_OF_DAY, 0);
                        expcal.set(Calendar.MINUTE, 0);
                        expcal.set(Calendar.SECOND, 0);
                        expcal.set(Calendar.MILLISECOND, 0);

                        long currentTime = cal.getTimeInMillis();
                        long expTime = expcal.getTimeInMillis();

                        long diffDays = (expTime - currentTime) / (1000 * 60 * 60 * 24);

                        Toast.makeText(reservation2.this, "Number of days left until expiration: " + diffDays, Toast.LENGTH_SHORT).show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }
        });



        /*
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        Date datee = new Date(timestamp);
        Calendar cal = Calendar.getInstance();
        int hourz = cal.get(Calendar.HOUR_OF_DAY);
        cal.setTime(datee);
        String current = simpleDateFormat.format(datee);
        String timedd;
        if(hourz >= 6 && hourz < 18)
        {
            timedd = "day";
        }
        else
        {
            timedd = "night";
        }

        Map<String, Object> map = new HashMap<>();
        map.put("currentTime", current);
        map.put("time", timedd);

        fs.collection("Petlover").document(email)
                .collection("appointments").document(servname)
                .update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        fs.collection("Petlover").document(email)
                                .collection("appointments").document(servname)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        String current2 = documentSnapshot.getString("currentTime");
                                        String[] current3 = current2.split(":");
                                        String expiration = documentSnapshot.getString("expiration");
                                        String[] expiration2 = expiration.split(":");
                                        String timeOfDayCur = documentSnapshot.getString("time");
                                        String timeofDayExp = documentSnapshot.getString("timed");

                                        int hours = Integer.parseInt(current3[0]);
                                        int minutes = Integer.parseInt(current3[1]);
                                        int totalMinutes = (hours * 60) + minutes;

                                        int hours2 = Integer.parseInt(expiration2[0]);
                                        int minutes2 = Integer.parseInt(expiration2[1]);
                                        int totalMinutes2 = (hours2 * 60) + minutes2;

         */

                                        /*

                                        if(totalMinutes > totalMinutes2)
                                        {
                                            if(timeOfDayCur.equals(timeofDayExp))
                                            {
                                                binding.btnCancelServ.setEnabled(false);
                                                Toast.makeText(reservation2.this, "Timer already expired!", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                        else
                                        {
                                            if(!timeOfDayCur.equals(timeofDayExp))
                                            {
                                                binding.btnCancelServ.setEnabled(false);
                                                Toast.makeText(reservation2.this, "Timer already expired!", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                binding.btnCancelServ.setEnabled(true);
                                            }

                                            //Toast.makeText(reservation2.this, "*", Toast.LENGTH_SHORT).show();
                                            //binding.btnCancelServ.setVisibility(View.VISIBLE);
                                        }

                                         */

/*
                                    }
                                });

                    }
                });

 */

    }

    public void choosePayment()
    {
        AlertDialog.Builder build = new AlertDialog.Builder(reservation2.this);
        //LayoutInflater inflate = reservation.this.getLayoutInflater();
        //View v = inflate.inflate(R.layout.deleteitem, null);
        build.setTitle("Select Payment Method");

        String [] options = {"E-wallet"};

        build.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selected = options[i];
                if(selected.equals("E-wallet"))
                {
                    String toTal = binding.servPrice.getText().toString();
                    String datePickUp = binding.servDate.getText().toString();
                    String ownerS = binding.ownerServ.getText().toString();

                    Intent intent = new Intent(reservation2.this, rayzorPayEntry.class);
                    intent.putExtra("ammount", toTal);
                    intent.putExtra("CPname", name);
                    intent.putExtra("owner", ownerS);
                    //intent.putExtra("quantity", receiptQuantity);
                    intent.putExtra("reservationDate", datePickUp);
                    intent.putExtra("schedule", scheduleBooked);
                    intent.putExtra("type", sType);
                    intent.putExtra("business Type", "Vet Clinic");

                    if(scheduleBooked == null)
                    {
                        Toast.makeText(reservation2.this, "No Schedule", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                    else
                    {
                        startActivity(intent);
                        dialogInterface.dismiss();
                    }

                }
                /*
                else
                {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    long timestamp = System.currentTimeMillis();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM:dd:yyyy:hh:mm:ss");
                    Date datee = new Date(timestamp);
                    Calendar cal = Calendar.getInstance();
                    //int hourz = cal.get(Calendar.HOUR_OF_DAY);
                    cal.setTime(datee);
                    String current = simpleDateFormat.format(datee);

                    String receiptName = binding.servName.getText().toString();
                    String receiptPrice = binding.servPrice.getText().toString();
                    String receiptDescript = binding.descriptServ.getText().toString();
                    //receiptQuantity = binding.quantityTextView.getText().toString();
                    //String receiptTotalPrice = binding.subtotalText.getText().toString();
                    String datePickUp2 = binding.servDate.getText().toString();
                    String ownerS2 = binding.ownerServ.getText().toString();


                    // Create a new document with the name and price
                    Map<String, Object> data = new HashMap<>();
                    data.put("email buyer", email);
                    data.put("name", receiptName);
                    data.put("schedule", scheduleBooked);
                    data.put("date paid", current);
                    //data.put("original price", receiptPrice);
                    data.put("description", receiptDescript);
                    //data.put("quantity", receiptQuantity);
                    data.put("total", receiptPrice);
                    //data.put("category", categoryProd);
                    data.put("type",sType);
                    data.put("business Type", "Vet Clinic");
                    data.put("owner", ownerS2);
                    data.put("payment","CASH");
                    data.put("pickup date", datePickUp2);
                    data.put("status", "unpaid");


                    // Add the document to the "payments" collection
                    db.collection("Petlover").document(email)
                            .collection("payment receipts")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    Toast.makeText(reservation2.this, "Your order will be paid in cash!", Toast.LENGTH_SHORT).show();
                                    //updateQuantityAfterSold();
                                    copyServ();
                                    startActivity(new Intent(reservation2.this, Home.class));
                                    //String delPayedDoc = documentReference.getId();


                                    db.collection("Petlover").document(email).collection("cart")
                                            .document(name).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    dialogInterface.dismiss();
                                                    startActivity(new Intent(reservation.this, mycart.class));
                                                }
                                            });



                                }
                            });
                }

                 */

            }
        });


        AlertDialog alertDialog = build.create();
        alertDialog.show();


    }

    public void copyServ()
    {
        DocumentReference docCopy = fs.collection("Petlover").document(email)
                .collection("appointments")
                .document(name);

        docCopy.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    // Get the document data as a Map object
                    Map<String, Object> document = task.getResult().getData();

                    // Create a new document in Firestore
                    DocumentReference docPaste = fs.collection("Petlover")
                            .document(email).collection("medical-history")
                            .document();

                    // Add the data to the new document
                    docPaste.set(document).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                delServ();

                            } else {

                                Toast.makeText(reservation2.this, "Something was wrong!", Toast.LENGTH_SHORT).show();
                                // Error occurred while copying and pasting the document
                            }
                        }
                    });
                } else {
                    // Error occurred while retrieving the original document
                    Toast.makeText(reservation2.this, "Error Process", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void delServ()
    {
        DocumentReference docDel = fs.collection("Petlover").document(email)
                .collection("appointments")
                .document(name);

        docDel.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

}