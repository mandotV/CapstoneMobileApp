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
import com.example.mymobileapp.databinding.ActivityReservationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class reservation extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private ActivityReservationBinding binding;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String email = firebaseUser.getEmail();

    String name, quantity, product_pp, orgprice;
    String categoryProd, ownerProd, BtypeProd, typeProd;
    String receiptQuantity;
    String stock;
    int quant = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name = getIntent().getStringExtra("name");
        //prodID = getIntent().getStringExtra("id");

        checkProd();
        //createNotification();
        //binding.btnPayProd.setEnabled(false);

        binding.increBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increment(view);
            }
        });

        binding.decreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrement(view);
            }
        });

        binding.dProdBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(reservation.this, mycart.class));
            }
        });

        binding.btnPayProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    choosePayment();

            }
        });

        binding.btnSaveProdEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });

        binding.btnDelProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delData();
            }
        });

        binding.cartPicker.setOnClickListener(new View.OnClickListener() {
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
            binding.btnPayProd.setEnabled(false);
        }
        else
        {
            binding.cartDateText.setText(selectedCartDate);
            binding.btnPayProd.setEnabled(true);
        }



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

            binding.quantityTextView.setText("" + num);

    }

    public void checkProd()
    {
        fs.collection("Petlover").document(email)
                .collection("cart").document(name)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        ownerProd = documentSnapshot.getString("owner");
                        categoryProd = documentSnapshot.getString("category");
                        BtypeProd = documentSnapshot.getString("business Type");
                        typeProd = documentSnapshot.getString("Type");
                        orgprice = documentSnapshot.getString("origprice");
                        product_pp = documentSnapshot.getString("cartlink");
                        quantity = documentSnapshot.getString("quantity");
                        String descript = documentSnapshot.getString("description");
                        Glide.with(reservation.this).load(product_pp)
                                .override(400,500)
                                .into(binding.prodPp);
                        binding.descriptProd.setText(descript);
                        binding.prodName.setText(name);
                        binding.prodPrice.setText(orgprice);
                        binding.quantityTextView.setText(quantity);
                        quant = Integer.parseInt(quantity);

                        double subTotal = Double.parseDouble(quantity) * Double.parseDouble(orgprice);
                        //ster = String.valueOf(subTotal);
                        String st = new DecimalFormat(".##").format(subTotal);

                        binding.subtotalText.setText(st);


                    }
                });

    }

    public void updateData()
    {
        String txtquant = binding.quantityTextView.getText().toString();
        double newTotal = Double.parseDouble(txtquant) * Double.parseDouble(orgprice);
        String st2 = new DecimalFormat(".##").format(newTotal);
        //String newprice = binding.subtotalText.getText().toString();

        Map<String, Object> map = new HashMap<>();
        map.put("quantity", txtquant);
        map.put("price", st2);
        if(Integer.parseInt(txtquant) < 0 || Integer.parseInt(txtquant) == 0)
        {
            binding.quantityTextView.setError("Quantity must not be below 0 or equals to zero!");
        }
        else
        {
            fs.collection("Petlover").document(email)
                    .collection("cart").document(name).update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(reservation.this, "Cart Updated!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(reservation.this, mycart.class));

                        }
                    });
        }

    }

    public void delData()
    {
        AlertDialog.Builder build = new AlertDialog.Builder(reservation.this);
        LayoutInflater inflate = reservation.this.getLayoutInflater();
        View v = inflate.inflate(R.layout.deleteitem, null);

        build.setView(v).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                fs.collection("Petlover").document(email).collection("cart")
                        .document(name)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(reservation.this, "Product Removed!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(reservation.this, mycart.class));
                            }
                        });
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(reservation.this, "Action cancelled!", Toast.LENGTH_SHORT).show();
                //finish();
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = build.create();
        build.show();




    }

    public void choosePayment()
    {
        AlertDialog.Builder build = new AlertDialog.Builder(reservation.this);
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
                    String toTal = binding.subtotalText.getText().toString();
                    String datePickUp = binding.cartDateText.getText().toString();
                    String quantityforE = binding.quantityTextView.getText().toString();

                    Intent intent = new Intent(reservation.this, rayzorPayEntry.class);
                    intent.putExtra("ammount", toTal);
                    intent.putExtra("CPname", name);
                    intent.putExtra("owner", ownerProd);
                    intent.putExtra("quantity", quantityforE);
                    intent.putExtra("reservationDate", datePickUp);
                    intent.putExtra("type", typeProd);
                    intent.putExtra("business Type", "Pet Store");

                    //Calendar cale = Calendar.getInstance();

                    if(datePickUp.equalsIgnoreCase("Date"))
                    {
                        Toast.makeText(reservation.this, "Date is not set!", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                        //binding.btnPayProd.setEnabled(false);
                    }
                    else if(Integer.parseInt(quantity) < 0 || Integer.parseInt(quantity) == 0)
                    {
                        Toast.makeText(reservation.this, "Quantity is below 0 or at 0!", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                    else
                    {
                        startActivity(intent);

                    }

                    //dialogInterface.dismiss();
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


                    String receiptName = binding.prodName.getText().toString();
                    String receiptPrice = binding.prodPrice.getText().toString();
                    String receiptDescript = binding.descriptProd.getText().toString();
                    receiptQuantity = binding.quantityTextView.getText().toString();
                    String receiptTotalPrice = binding.subtotalText.getText().toString();
                    String datePickUp2 = binding.cartDateText.getText().toString();


                    // Create a new document with the name and price
                    Map<String, Object> data = new HashMap<>();
                    data.put("email buyer", email);
                    data.put("name", receiptName);
                    data.put("original price", receiptPrice);
                    data.put("description", receiptDescript);
                    data.put("quantity", receiptQuantity);
                    data.put("total", receiptTotalPrice);
                    data.put("category", categoryProd);
                    data.put("type", typeProd);
                    data.put("business Type", BtypeProd);
                    data.put("owner", ownerProd);
                    data.put("payment","CASH");
                    data.put("pickup date", datePickUp2);
                    data.put("date paid", current);
                    data.put("status", "unpaid");

                    // Add the document to the "payments" collection
                    //Calendar cale = Calendar.getInstance();

                    if(datePickUp2.equalsIgnoreCase("Date"))
                    {
                        Toast.makeText(reservation.this, "Date is not set!", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                    else if(Integer.parseInt(quantity) < 0 || Integer.parseInt(quantity) == 0)
                    {
                        Toast.makeText(reservation.this, "Quantity is below 0 or at 0!", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(reservation.this, "Your order will be paid in cash!", Toast.LENGTH_SHORT).show();
                                        updateQuantityAfterSold();
                                        copyProd();
                                        startActivity(new Intent(reservation.this, Home.class));
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

                }
                */

            }
        });


        AlertDialog alertDialog = build.create();
        alertDialog.show();


    }

    public void copyProd()
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

                                Map<String, Object> updates = new HashMap<>();
                                updates.put("payment", "CASH");

                                docPaste.update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                delProd();
                                            }
                                        });



                            } else {

                                Toast.makeText(reservation.this, "Something was wrong!", Toast.LENGTH_SHORT).show();
                                // Error occurred while copying and pasting the document
                            }
                        }
                    });
                } else {
                    // Error occurred while retrieving the original document
                    Toast.makeText(reservation.this, "Error Process", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void addPaymentToHistory()
    {

    }

    public void delProd()
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

    public void updateQuantityAfterSold()
    {
        DocumentReference docDel = fs.collection("petstore").document(ownerProd)
                .collection("products")
                .document(name);


        docDel.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                stock = documentSnapshot.getString("Stock");

                if(stock != null && quantity != null)
                {
                    int stockInStore = Integer.parseInt(stock);
                    int stockInCart = Integer.parseInt(quantity);
                    int newStock = stockInStore - stockInCart;

                    Map<String, Object> map = new HashMap<>();
                    map.put("Stock", String.valueOf(newStock));

                    docDel.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
                }

            }
        });


    }

}