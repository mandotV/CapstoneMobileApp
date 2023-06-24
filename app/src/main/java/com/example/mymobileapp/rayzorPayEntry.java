package com.example.mymobileapp;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class rayzorPayEntry extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = rayzorPayEntry.class.getSimpleName();
    String ammount, Cname, Eowner, Equant, Edate, Etype, Esched, EBtype;
    HashMap hash = new HashMap();

    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    String email = firebaseUser.getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rayzor_pay_entry);

        Checkout.preload(getApplicationContext());
        ammount = getIntent().getStringExtra("ammount");
        Cname = getIntent().getStringExtra("CPname");
        Eowner = getIntent().getStringExtra("owner");
        Equant = getIntent().getStringExtra("quantity");
        Edate = getIntent().getStringExtra("reservationDate");
        Esched = getIntent().getStringExtra("schedule");
        Etype = getIntent().getStringExtra("type");
        EBtype = getIntent().getStringExtra("business Type");

        startPayment(Double.parseDouble(ammount));
        createNotification();



    }

    public void notification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "E-Payment")
                .setSmallIcon(R.drawable.logo)
                .setShowWhen(true)
                .setContentTitle("E-Payment")
                .setContentText("You have successfully paid the product!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(rayzorPayEntry.this);
        managerCompat.notify(1, builder.build());
    }

    public void createNotification()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("E-Wallet", "E-Wallet", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }


    public void startPayment(double am) {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_mFZzdtSo4P7vOs");
        //secret : tLV5OkEWD4J8nBgYaK0RR7jF
        /**
         * Instantiate Checkout
         */
       //Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "PETik E-payment");
            options.put("description", "Put your pets first.");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
            options.put("order_id", "");//from response of step 3.
            options.put("theme.color", "#cc6633");
            options.put("currency", "PHP");
            options.put("amount", am*100);//pass amount in currency subunits
            options.put("prefill.email", email);
            options.put("prefill.contact","");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 10);
            options.put("retry", retryObj);

            checkout.open(activity, options);


            String contactE = options.getString("prefill.email");
            String contactN = options.getString("prefill.contact");
            //String amount = options.getString("amount");
            String currency = options.getString("currency");


            long timestamp = System.currentTimeMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM:dd:yyyy:hh:mm:ss");
            Date datee = new Date(timestamp);
            Calendar cal = Calendar.getInstance();
            //int hourz = cal.get(Calendar.HOUR_OF_DAY);
            cal.setTime(datee);
            String current = simpleDateFormat.format(datee);

            hash.put("email buyer", contactE);
            hash.put("quantity", Equant);
            hash.put("contact number", contactN);
            hash.put("amount", ammount);
            hash.put("currency", currency);
            hash.put("payment", "E-WALLET");
            hash.put("owner", Eowner);
            hash.put("date paid", current);
            hash.put("reservation date", Edate);
            hash.put("name", Cname);
            hash.put("type", Etype);
            hash.put("schedule", Esched);
            hash.put("business Type", EBtype);
           // hash.put("business name", bn);




        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(rayzorPayEntry.this, mycart.class));
    }

    public void copyDocs()
    {

        if(Etype.equalsIgnoreCase("Pet") || Etype.equalsIgnoreCase("Product"))
        {
            DocumentReference docCopy = fs.collection("Petlover").document(email)
                    .collection("cart")
                    .document(Cname);

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
                                    updates.put("payment", "E-WALLET");

                                    docPaste.update(updates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    delDocs();
                                                }
                                            });


                                } else {

                                    Toast.makeText(rayzorPayEntry.this, "Something was wrong!", Toast.LENGTH_SHORT).show();
                                    // Error occurred while copying and pasting the document
                                }
                            }
                        });
                    } else {
                        // Error occurred while retrieving the original document
                        Toast.makeText(rayzorPayEntry.this, "Error Process", Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
        else if(Etype.equalsIgnoreCase("Service"))
        {
            DocumentReference docCopy = fs.collection("Petlover").document(email)
                    .collection("appointments")
                    .document(Cname);

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

                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("payment", "E-WALLET");

                                    docPaste.update(updates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    delDocsServ();
                                                }
                                            });


                                } else {

                                    Toast.makeText(rayzorPayEntry.this, "Something was wrong!", Toast.LENGTH_SHORT).show();
                                    // Error occurred while copying and pasting the document
                                }
                            }
                        });
                    } else {
                        // Error occurred while retrieving the original document
                        Toast.makeText(rayzorPayEntry.this, "Error Process", Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }

    }

    public void delDocs()
    {
        DocumentReference docDel = fs.collection("Petlover").document(email)
                .collection("cart")
                .document(Cname);

            docDel.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
    }
    public void delDocsServ()
    {
        DocumentReference docDelServ = fs.collection("Petlover").document(email)
                .collection("appointments")
                .document(Cname);

        docDelServ.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public void updateQuantityAfterSold()
    {
        DocumentReference docUpdateStock = fs.collection("petstore").document(Eowner)
                .collection("products")
                .document(Cname);
        docUpdateStock.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String stock = documentSnapshot.getString("Stock");
                if(stock != null && Equant != null)
                {
                    int stockInStore = Integer.parseInt(stock);
                    int stockInCart = Integer.parseInt(Equant);
                    int newStock = stockInStore - stockInCart;

                    Map<String, Object> map = new HashMap<>();
                    map.put("Stock", String.valueOf(newStock));

                    docUpdateStock.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(rayzorPayEntry.this, "Stock is empty!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onPaymentSuccess(String paymentId) {


        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();

        if(Etype.equalsIgnoreCase("Product"))
        {
            updateQuantityAfterSold();

        }
        copyDocs();

        fs.collection("Petlover").document(email)
                .collection("payment receipts")
                .document(paymentId).set(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        notification();
                        startActivity(new Intent(rayzorPayEntry.this, Home.class));

                        //finish();

                    }
                });
        /*
        else
        {

        copyDocs();

        fs.collection("Petlover").document(email)
                .collection("payment receipts")
                .document(paymentId).set(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        startActivity(new Intent(rayzorPayEntry.this, mycart.class));
                        //finish();

                    }
                });
        }

         */

    }


    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(this, "Payment Error!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(rayzorPayEntry.this, mycart.class));
        finish();

    }
}