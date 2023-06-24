package com.example.mymobileapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mymobileapp.databinding.ActivityEmailVerifyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class emailVerify extends AppCompatActivity {

    private ActivityEmailVerifyBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityEmailVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        binding.buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verifyEmail();
                //startActivity(new Intent(emailVerify.this, Home.class));
                //finish();

            }
        });





    }




    private void verifyEmail(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    Toast.makeText(emailVerify.this,
                            "Verification email sent to " + firebaseUser.getEmail(),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.e(TAG,"Send Email Verification", task.getException());
                    Toast.makeText(emailVerify.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}