package com.example.mymobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mymobileapp.databinding.ActivityReceiptDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

public class receiptDetails extends AppCompatActivity {

    private ActivityReceiptDetailsBinding binding;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    String paymenType, totalPaymen, itemPaymen, itemType, itemQuantity, pickUpDate, itemOwner, schedDate, datePaid;
    String type, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiptDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        schedDate = getIntent().getStringExtra("schedule");
        paymenType = getIntent().getStringExtra("payment");
        totalPaymen = getIntent().getStringExtra("amount");
        itemPaymen = getIntent().getStringExtra("name");
        itemType = getIntent().getStringExtra("type");
        pickUpDate = getIntent().getStringExtra("pickup date");
        itemOwner = getIntent().getStringExtra("owner");
        itemQuantity = getIntent().getStringExtra("quantity");
        datePaid = getIntent().getStringExtra("date paid");

        id = getIntent().getStringExtra("docId");


        type = getIntent().getStringExtra("btype");

        if(itemQuantity == null)
        {
            itemQuantity = "1";
        }
        if(schedDate == null)
        {
            schedDate = "N/A";
        }

        showData();

        if(paymenType.equalsIgnoreCase("CASH"))
        {
            binding.cancelItem.setText("Cancel");

        }
        else
        {
            binding.cancelItem.setText("Delete");
        }



        binding.cancelItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    deleteReceipt();
                    startActivity(new Intent(receiptDetails.this, Home.class));

            }
        });



    }

    public void showData()
    {

        double priceForItem = Double.parseDouble(totalPaymen) / Integer.parseInt(itemQuantity);
        double vatSales = Double.parseDouble(totalPaymen) / 1.12;
        double vatAmount = vatSales * .12;

        String decVS = new DecimalFormat(".##").format(vatSales);
        String decVA = new DecimalFormat(".##").format(vatAmount);

        binding.VatPerc.setText("12%");



        binding.ValSales.setText(decVS);
        binding.VatAmount.setText(decVA);
        binding.pricePerItem.setText(String.valueOf(priceForItem));
        binding.BtypePayment.setText(paymenType);
        binding.totalPayment.setText(totalPaymen);
        binding.typePayment.setText(itemType);
        binding.puDate.setText(datePaid);
        binding.itemName.setText(itemPaymen);
        binding.itemOwner.setText(itemOwner);
        binding.itemquant.setText(itemQuantity);

        String modifiedString = type.trim().replaceAll("\\s+", "").toLowerCase();

        if(modifiedString.equalsIgnoreCase("petstore"))
        {

            binding.schedDate.setText(pickUpDate);
        }
        else
        {

            binding.schedDate.setText(schedDate);
        }

        DocumentReference fetchAdd = fs.collection(modifiedString).document(itemOwner);

        fetchAdd.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String addressOwner = documentSnapshot.getString("address");
                String latitude = documentSnapshot.getString("lati");
                String longitude = documentSnapshot.getString("long");

                String newLati = new DecimalFormat(".#####").format(Double.parseDouble(latitude));
                String newLongi = new DecimalFormat(".#####").format(Double.parseDouble(longitude));
                binding.ownerAddress.setText(addressOwner);
                binding.ownerLati.setText(newLati);
                binding.ownerLongi.setText(newLongi);


            }
        });
    }

    public void deleteReceipt()
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fu = firebaseAuth.getCurrentUser();
        String email = fu.getEmail();

        DocumentReference docDel = fs.collection("Petlover").document(email)
                .collection("payment receipts").document(id);

        docDel.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

}