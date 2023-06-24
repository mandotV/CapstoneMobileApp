package com.example.mymobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import com.example.mymobileapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth firebaseAuth;

    private String email = "", password = "" , password2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valid();

            }

        });
        binding.txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, Login.class));
            }

        });

        binding.btnEyeSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.plPassword.getTransformationMethod() instanceof PasswordTransformationMethod &&
                        binding.plCPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    binding.plPassword.setTransformationMethod(null);
                    binding.plCPassword.setTransformationMethod(null);
                } else {
                    binding.plPassword.setTransformationMethod(new PasswordTransformationMethod());
                    binding.plCPassword.setTransformationMethod(new PasswordTransformationMethod());
                }

            }
        });
    }

    private void valid() {

        email = binding.plEmail.getText().toString().trim();
        password = binding.plPassword.getText().toString().trim();
        password2 = binding.plCPassword.getText().toString().trim();


        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.plEmail.setError("Invalid email");
        }
        else if (password.length()<8){
            binding.plPassword.setError("Short password");
        }
        else if (TextUtils.isEmpty(password)){
            binding.plPassword.setError("Blank Password");
        }
        else if (TextUtils.isEmpty(email)){
            binding.plEmail.setError("Blank Email");
        }
        else if (!password.equalsIgnoreCase(password2))
        {
            binding.plCPassword.setError("Password not the same");
        }
        else if(!password.matches("^.*[^a-zA-Z0-9].*$"))
        {
            binding.plPassword.setError("Password should contain special characters");
        }
        else if (!password.matches(".*[A-Z].*")) {
            binding.plPassword.setError("Password should contain atleast a lowercase or an uppercase character");
        }
        else {
            firebaseSignUp();
        }
    }

    private void firebaseSignUp() {

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String email = firebaseUser.getEmail();
                        Toast.makeText(SignUp.this, "Account Created " + email, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this, Login.class));

                     /*
                        if(firebaseUser.isEmailVerified()){

                            firebaseUser.sendEmailVerification();
                            startActivity(new Intent(SignUp.this, Login.class));
                            Toast.makeText(SignUp.this, "Please check your email!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                      */



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                });
    }

}