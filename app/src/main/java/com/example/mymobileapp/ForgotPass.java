package com.example.mymobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.mymobileapp.databinding.ActivityForgotPassBinding;
import com.example.mymobileapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity {

    private ActivityForgotPassBinding binding;
    private FirebaseAuth firebaseAuth;
    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetpassword();
            }
        });

    }

    private void resetpassword(){

        email = binding.forgotEmail.getText().toString().trim();

        if(email.isEmpty()){

            binding.forgotEmail.setError("Email is required!");
            binding.forgotEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.forgotEmail.setError("Provide valid Email!");
            binding.forgotEmail.requestFocus();
            return;
        }

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(ForgotPass.this, Login.class));
                    Toast.makeText(ForgotPass.this, "Check your email!", Toast.LENGTH_SHORT).show();
                }
                else{

                    Toast.makeText(ForgotPass.this, "Try again, Something is wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}