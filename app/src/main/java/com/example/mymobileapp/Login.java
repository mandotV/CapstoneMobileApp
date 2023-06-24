package com.example.mymobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import com.example.mymobileapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {


    private ActivityLoginBinding binding;
    private String email = "", password = "";
    private FirebaseAuth firebaseAuth;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        //checkUser();
        binding.plPassword.setTransformationMethod(new PasswordTransformationMethod());

       // Drawable buttonDrawable = getResources().getDrawable(R.drawable.ic_baseline_remove_red_eye_24);
        //binding.btnEye.setCompoundDrawablesWithIntrinsicBounds(null, null, buttonDrawable, null);

        binding.btnEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.plPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    binding.plPassword.setTransformationMethod(null);
                } else {
                    binding.plPassword.setTransformationMethod(new PasswordTransformationMethod());
                }

            }
        });

        binding.txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, SignUp.class));
            }
        });

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }

        });

        binding.txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ForgotPass.class));

            }
        });
    }

/*
    private void checkUser(){

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(this, Home.class));
            //finish();
        }
        else
        {
            Toast.makeText(this, "You can now login!", Toast.LENGTH_SHORT).show();
        }
    }

 */





    private void validateData(){
        email = binding.plEmail.getText().toString().trim();
        password = binding.plPassword.getText().toString().trim();

        /*if(firebaseAuth.getCurrentUser() == null)
        {
            Toast.makeText(this, "Email not registered!", Toast.LENGTH_SHORT).show();
        }
        else */
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            binding.plEmail.setError("Invalid email");

        }
        else if (password.length()<6){
            binding.plEmail.setError("Short password");
        }
        else if (TextUtils.isEmpty(password)){
            binding.plPassword.setError("Blank Password");

        }
        else {
            firebaseLogin();

        }

    }

    private void firebaseLogin() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {

                            if(firebaseAuth.getCurrentUser().isEmailVerified())
                            {
                                String email = firebaseAuth.getCurrentUser().getEmail();
                                progressLogin();
                                Toast.makeText(Login.this, "Logged in as " + email, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, Home.class));
                            }
                            else
                            {
                                firebaseAuth.getCurrentUser().sendEmailVerification();
                                firebaseAuth.signOut();
                                showAlert();
                            }

                        }
                        else
                        {
                            try
                            {
                                throw task.getException();

                            } catch (FirebaseAuthInvalidUserException e )
                            {
                                binding.plEmail.setError("User does not exist or is no longer invalid. Please register again");
                                binding.plEmail.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e)
                            {
                                binding.plEmail.setError("Invalid Credentials. Kindly, check and re-enter.");
                            }
                            catch (Exception e) {
                                Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    
                });


                /*.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {


                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String email = firebaseUser.getEmail();
                        Toast.makeText(Login.this, "Logged in as " + email, Toast.LENGTH_SHORT).show();


                        startActivity(new Intent(Login.this, Home.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        Toast.makeText(Login.this, "Wrong password or email", Toast.LENGTH_SHORT).show();
                    }
                });

                 */
    }

    private void showAlert() {

        AlertDialog.Builder build = new AlertDialog.Builder(Login.this);
        build.setTitle("Email not verified!");
        build.setMessage("Please check email your email, you cannot login if not verified!");

        build.setPositiveButton("Check email!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alert = build.create();
        build.show();
    }




    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified())
        {
            startActivity(new Intent(Login.this, Home.class));
        }
        else
        {
            Toast.makeText(this, "You can now login!", Toast.LENGTH_SHORT).show();
        }
        }

    public void progressLogin()
    {
        pd = new ProgressDialog(this);
        pd.setMessage("Logging in....^_^");
        pd.setCancelable(false);

        // Show the progress dialog
        pd.show();

        // Perform the login process in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Simulate a long running task
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Dismiss the progress dialog
                pd.dismiss();
            }
        }).start();
    }







}