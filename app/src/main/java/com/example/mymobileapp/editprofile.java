package com.example.mymobileapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.mymobileapp.databinding.ActivityEditprofileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class editprofile extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ActivityEditprofileBinding binding;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String email;
    int result;
    String selectedBirthdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditprofileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        email = firebaseUser.getEmail();

        binding.profileEmail.setText(email);

        binding.profileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(editprofile.this, profile.class));
            }
        });

        binding.agePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datepicker = new datepicker();
                datepicker.show(getSupportFragmentManager(), "Set your birthdate");
            }
        });


        firestore.collection("Petlover").document(email+"").get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String name = documentSnapshot.getString("Name");
                        String age = documentSnapshot.getString("Age");
                        String address = documentSnapshot.getString("Address");
                        String number = documentSnapshot.getString("Mobile");
                        String bdate = documentSnapshot.getString("Birthdate");


                        binding.editName.setText(name);
                        binding.editAge.setText(bdate);
                        binding.editAddress.setText(address);
                        binding.editNumber.setText(number);

                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                        try {

                            Date date = sdf.parse(bdate);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            int month = calendar.get(Calendar.MONTH);
                            int year = calendar.get(Calendar.YEAR);

                            //Toast.makeText(editprofile.this, "" + date, Toast.LENGTH_SHORT).show();

                            Calendar calendarCurrent = Calendar.getInstance();
                            int currentYear = calendarCurrent.get(Calendar.YEAR);
                            int currentMonth = calendarCurrent.get(Calendar.MONTH);
                            int currentDay = calendarCurrent.get(Calendar.DATE);

                            selectedBirthdate = sdf.format(calendar.getTime());

                            if(currentMonth == month && currentDay == day)
                            {
                                result = currentYear - year;
                                //binding.editAge.setText(result);
                            }
                            else if(currentMonth > month && currentDay > day)
                            {
                                result = currentYear - year;
                                //binding.editAge.setText(result);
                            }
                            else
                            {
                                result = currentYear - year;
                                result = result - 1;
                                //binding.editAge.setText(result);
                            }

                            binding.editAge.setText(selectedBirthdate);


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(editprofile.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });

       // firestore.collection("profileBirth").document(email+"").get()




        binding.btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProf();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(editprofile.this, Home.class));
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DATE);

        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");



        selectedBirthdate = dateFormat.format(c.getTime());

        if(currentMonth == month && currentDay == day)
        {
            result = currentYear - year;
            //binding.editAge.setText(result);
        }
        else if(currentMonth > month && currentDay > day)
        {
            result = currentYear - year;
            //binding.editAge.setText(result);
        }
        else
        {
            result = currentYear - year;
            result = result - 1;
            //binding.editAge.setText(result);
        }



        binding.editAge.setText(selectedBirthdate);



    }

    public void saveProf(){

        String name = binding.editName.getText().toString();
        String address = binding.editAddress.getText().toString();
        //String age = binding.editAge.getText().toString();
        String number = binding.editNumber.getText().toString();
        //String bdate = binding.editAge.getText().toString();

        String contactNum = binding.editNumber.getText().toString().trim();


        HashMap hashMap = new HashMap();
        hashMap.put("Email", email);
        hashMap.put("Name", name);
        hashMap.put("Address", address);
        hashMap.put("Age", String.valueOf(result));
        hashMap.put("Mobile", number);
        hashMap.put("Birthdate", selectedBirthdate);


        //saveChanges();

        firestore.collection("Petlover").document( email +"").update(hashMap).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        if (result < 18) {
                           // binding.editAge.setError("");
                            Toast.makeText(editprofile.this, "Age must be atleast 18!", Toast.LENGTH_SHORT).show();
                        } else if (contactNum.length() < 11) {
                            binding.editNumber.setError("Must be atleast 11 numbers!");
                        } else {
                            Toast.makeText(editprofile.this, "Profile saved!", Toast.LENGTH_SHORT).show();
                            //saveBirthDate();

                            startActivity(new Intent(editprofile.this, profile.class));
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(editprofile.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });

    }


}