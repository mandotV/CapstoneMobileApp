package com.example.mymobileapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mymobileapp.databinding.ActivityVetlistBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class vetlist extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private ActivityVetlistBinding binding;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    String clinicUser,vName, vPhone, vEmail, vImage, vStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVetlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        clinicUser = getIntent().getStringExtra("username");
        vName = getIntent().getStringExtra("name");
        vPhone = getIntent().getStringExtra("phone");
        vEmail = getIntent().getStringExtra("email");
        vImage = getIntent().getStringExtra("image");

        DocumentReference docCheckStatus = fs.collection("vetclinic").document(clinicUser)
                .collection("vets").document(vName);

        docCheckStatus.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                vStatus = documentSnapshot.getString("status");

                binding.vetStatus.setText(vStatus);

            }
        });

        binding.veterName.setText(vName);
        binding.veterEmail.setText(vEmail);
        binding.veterPhone.setText(vPhone);
        Glide.with(vetlist.this).load(vImage)
                .override(700, 700)
                .into(binding.veterImage);

        chechSched();
        availableVet();





    }

    public void availableVet()
    {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();

        CollectionReference checkVetAvail = fs.collection("Petlover").document(email)
                .collection("appointments");
        DocumentReference docUpdateStatus = fs.collection("vetclinic").document(clinicUser)
                .collection("vets").document(vName);


        Query quer = checkVetAvail.whereEqualTo("vet", vName);

        quer.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful())
                {
                    QuerySnapshot snapshots = task.getResult();
                    if (snapshots.size() == 0)
                    {
                        HashMap m = new HashMap();
                        m.put("status", "Available");

                        docUpdateStatus.update(m).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {

                               // binding.vetStatus.setText("Available");

                            }
                        });
                    }
                    else
                    {
                        HashMap m = new HashMap();
                        m.put("status", "Busy");

                        docUpdateStatus.update(m).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                              //  binding.vetStatus.setText("Busy");
                            }
                        });
                    }
                }


            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void chechSched()
    {
        List<String> subjects = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.veterSched.setAdapter(adapter);
        binding.veterSched.setOnItemSelectedListener(vetlist.this);


        DocumentReference docGetVet = fs.collection("vetclinic").document(clinicUser).collection("vets")
                .document(vName);

        docGetVet.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists())
                {

                    //List<String> tags = (List<String>) documentSnapshot.get("tags");
                    String mon = documentSnapshot.getString("Monday");
                    String tue = documentSnapshot.getString("Tuesday");
                    String wed = documentSnapshot.getString("Wednesday");
                    String thur = documentSnapshot.getString("Thursday");
                    String fri = documentSnapshot.getString("Friday");
                    String sat = documentSnapshot.getString("Saturday");
                    String sun = documentSnapshot.getString("Sunday");

                    if(mon!=null)
                    {
                        subjects.add("Monday: "+ mon);
                    }
                    if(tue!=null)
                    {
                        subjects.add("Tuesday: "+tue);
                    }
                    if(wed!=null)
                    {
                        subjects.add("Wednesday: "+wed);
                    }
                    if(thur!=null)
                    {
                        subjects.add("Thursday: "+thur);
                    }
                    if(fri!=null)
                    {
                        subjects.add("Friday: "+fri);
                    }
                    if(sat!=null)
                    {
                        subjects.add("Saturday: "+sat);
                    }
                    if(sun!=null)
                    {
                        subjects.add("Sunday: "+ sun);
                    }
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(vetlist.this, "No schedule assigned!", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(vetlist.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}