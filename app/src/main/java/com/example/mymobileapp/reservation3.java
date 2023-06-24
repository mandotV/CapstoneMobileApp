package com.example.mymobileapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.mymobileapp.databinding.ActivityReservation3Binding;
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
import java.util.Calendar;
import java.util.Map;

public class reservation3 extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ActivityReservation3Binding binding;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String email = firebaseUser.getEmail();

    String name, ownerPet, pType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservation3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name = getIntent().getStringExtra("name");
        pType = "Pet";

        showpetcartdata();

        binding.btnPayPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*

                String toTal = binding.petprice2.getText().toString();

                Intent intent = new Intent(reservation3.this, rayzorPayEntry.class);
                intent.putExtra("ammount", toTal);


                startActivity(intent);

                 */
                    choosePayment();


            }
        });

        binding.btnDelPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delPet();
            }
        });

        binding.petPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datepicker = new datepicker();
                datepicker.show(getSupportFragmentManager(), "Set Pick-Up date");
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

        if(c.before(currentDate))
        {
            Toast.makeText(this, "Date has already passed!", Toast.LENGTH_SHORT).show();
            binding.btnPayPet.setEnabled(false);
        }
        else
        {
            binding.petDateText.setText(selectedCartDate);
            binding.btnPayPet.setEnabled(true);
        }







    }

    private void showpetcartdata() {

        fs.collection("Petlover").document(email).collection("cart")
                .document(name).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String pBdate = documentSnapshot.getString("birthdate");
                        String pAge = documentSnapshot.getString("age");
                        String pWeight = documentSnapshot.getString("weight");
                        String pDescription = documentSnapshot.getString("description");
                        String pColor = documentSnapshot.getString("color");
                        String pbred = documentSnapshot.getString("breed");
                        String pSex = documentSnapshot.getString("sex");
                        String pprce = documentSnapshot.getString("price");
                        String ppimage = documentSnapshot.getString("cartlink");
                        ownerPet = documentSnapshot.getString("owner");


                        binding.petName2.setText(name);
                        binding.petBdate2.setText(pBdate);
                        binding.petBreed2.setText(pbred);
                        binding.petSex2.setText(pSex);
                        binding.petAge2.setText(pAge);
                        binding.petWeight2.setText(pWeight);
                        binding.petDesc2.setText(pDescription);
                        binding.petColor2.setText(pColor);
                        binding.petprice2.setText(pprce);
                        Glide.with(reservation3.this).load(ppimage)
                                .into(binding.petPp2);





                    }
                });

    }

    public void delPet()
    {
        AlertDialog.Builder build = new AlertDialog.Builder(reservation3.this);
        LayoutInflater inflate = reservation3.this.getLayoutInflater();
        View v = inflate.inflate(R.layout.deletepet, null);

        build.setView(v).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                fs.collection("Petlover").document(email).collection("cart")
                        .document(name)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(reservation3.this, "Pet Removed!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(reservation3.this, mycart.class));
                            }
                        });
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(reservation3.this, "Action cancelled!", Toast.LENGTH_SHORT).show();
                //finish();
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = build.create();
        build.show();



    }


    public void choosePayment()
    {
        AlertDialog.Builder build = new AlertDialog.Builder(reservation3.this);
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
                    String toTal = binding.petprice2.getText().toString();
                    String datePickUp = binding.petDateText.getText().toString();

                    Intent intent = new Intent(reservation3.this, rayzorPayEntry.class);
                    intent.putExtra("ammount", toTal);
                    intent.putExtra("CPname", name);
                    intent.putExtra("owner", ownerPet);
                    //intent.putExtra("quantity", "1");
                    intent.putExtra("reservationDate", datePickUp);
                    intent.putExtra("type", pType);
                    intent.putExtra("business Type", "Pet Store");

                    if(datePickUp.equalsIgnoreCase("Date"))
                    {
                        Toast.makeText(reservation3.this, "Date is not set!", Toast.LENGTH_SHORT).show();
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

                    String receiptName = binding.petName2.getText().toString();
                    String receiptPrice = binding.petprice2.getText().toString();
                    String receiptDescript = binding.petDesc2.getText().toString();
                    //receiptQuantity = binding.quantityTextView.getText().toString();
                    //String receiptTotalPrice = binding.subtotalText.getText().toString();
                    String datePickUp2 = binding.petDateText.getText().toString();


                    // Create a new document with the name and price
                    Map<String, Object> data = new HashMap<>();
                    data.put("email buyer", email);
                    data.put("name", receiptName);
                    //data.put("original price", receiptPrice);
                    data.put("description", receiptDescript);
                    //data.put("quantity", receiptQuantity);
                    data.put("total", receiptPrice);
                    //data.put("category", categoryProd);
                    data.put("date paid", current);
                    data.put("type", pType);
                    data.put("business Type", "Pet Store");
                    data.put("owner", ownerPet);
                    data.put("payment","CASH");
                    data.put("pickup date", datePickUp2);
                    data.put("status", "unpaid");


                    if(datePickUp2.equalsIgnoreCase("Date"))
                    {
                        Toast.makeText(reservation3.this, "Date is not set!", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                    else
                    {
                        db.collection("Petlover").document(email)
                                .collection("payment receipts")
                                .add(data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        Toast.makeText(reservation3.this, "Your order will be paid in cash!", Toast.LENGTH_SHORT).show();
                                        //updateQuantityAfterSold();
                                        copyPet();
                                        startActivity(new Intent(reservation3.this, Home.class));
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


                    // Add the document to the "payments" collection

                }

                 */

            }
        });


        AlertDialog alertDialog = build.create();
        alertDialog.show();


    }

    public void copyPet()
    {
        DocumentReference docCopy = fs.collection("Petlover").document(email)
                .collection("cart")
                .document(name);

        docCopy.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    // Get the document data as a Map object
                    Map<String, Object> document = task.getResult().getData();

                    // Create a new document in Firestore
                    DocumentReference docPaste = fs.collection("Petlover")
                            .document(email).collection("cart-history")
                            .document();

                    // Add the data to the new document
                    docPaste.set(document).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                delProdPet();
                                delPetInventory();

                            } else {

                                Toast.makeText(reservation3.this, "Something was wrong!", Toast.LENGTH_SHORT).show();
                                // Error occurred while copying and pasting the document
                            }
                        }
                    });
                } else {
                    // Error occurred while retrieving the original document
                    Toast.makeText(reservation3.this, "Error Process", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void delProdPet()
    {
        DocumentReference docDel = fs.collection("Petlover").document(email)
                .collection("cart")
                .document(name);

        docDel.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
    public void delPetInventory()
    {
        DocumentReference docDel = fs.collection("petstore").document(ownerPet)
                .collection("pets")
                .document(name);

        docDel.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
}