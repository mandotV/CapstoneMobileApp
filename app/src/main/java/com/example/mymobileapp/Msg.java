package com.example.mymobileapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Msg extends AppCompatActivity implements interfacemsg {

    RecyclerView recyclerView;
    ArrayList<msgData> msgDataArrayList;
    adapterMsg adapterMsg;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    String email, img;
    String receiver, getrec;
    TextView r;
    EditText edtemail;
    ImageView back;
    ImageView pid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        getrec = getIntent().getStringExtra("receiver");
        img = getIntent().getStringExtra("imagelink");


        back = findViewById(R.id.back_home);

        LayoutInflater inflate = Msg.this.getLayoutInflater();
        View v = inflate.inflate(R.layout.item_msg,null);
        pid = v.findViewById(R.id.profileId);

        email = firebaseUser.getEmail();

        recyclerView = findViewById(R.id.message_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        msgDataArrayList = new ArrayList<msgData>();
        adapterMsg = new adapterMsg(Msg.this, msgDataArrayList, this);

        recyclerView.setAdapter(adapterMsg);


        EventChangeListener();
        //Toast.makeText(this, "" + receiver, Toast.LENGTH_SHORT).show();
        //profIdd();
        
        if(getrec != null)
        {
            addMessage();

        }
        else
        {
            Toast.makeText(this, "^_^", Toast.LENGTH_SHORT).show();
        }
        

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Msg.this, Home.class));
            }
        });

    }


    public void addMessage(){
        AlertDialog.Builder build = new AlertDialog.Builder(Msg.this);
        LayoutInflater inflate = Msg.this.getLayoutInflater();
        View v = inflate.inflate(R.layout.popupdialog_form, null);

        build.setView(v).setPositiveButton("add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                //receiver = edtemail.getText().toString();
                edtemail.setText(getrec);
                Long tm = System.currentTimeMillis();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
                Date date = new Date(tm);
                String tm2 = simpleDateFormat.format(date);

                /*

                                    HashMap hash2 = new HashMap();
                                    hash2.put("id", email);
                                    hash2.put("outletType", outlet);
                                    hash2.put("message", msg);
                                    hash2.put("receiver", receiver);

                                    fs.collection("Petlover").document( + "").set(hash2)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                        }
                                                    });

                 */




                                    HashMap hash = new HashMap();
                                    hash.put("id", email);
                                    hash.put("sender", email);
                                    hash.put("receiver", getrec);
                                    hash.put("timestamp", tm2);
                                    hash.put("pp", img);

                                    hash.put("statusmobile","ACTIVE");

                                    fs.collection("Petlover").document(email + "")
                                            .collection("messages").document(getrec)
                                            .set(hash)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    Toast.makeText(Msg.this, "Added to message list!", Toast.LENGTH_SHORT).show();

                                                }
                                            });



            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(Msg.this, "Action cancelled!", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();

            }
        });

        edtemail = v.findViewById(R.id.diag_email);
        edtemail.setText(getrec);



        AlertDialog alertDialog = build.create();
        build.show();
    }

    private void EventChangeListener() {

        fs.collection("Petlover").document(email).collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                        if(error !=null){
                            Log.e("Error",error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()){
/*
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    msgDataArrayList.add(dc.getDocument().toObject(msgData.class));
                                    adapterMsg.notifyDataSetChanged();
                            }

 */
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                msgData msg = dc.getDocument().toObject(msgData.class);
                                if (!msg.getStatusmobile().equalsIgnoreCase("DELETED")) {
                                    msgDataArrayList.add(msg);
                                    adapterMsg.notifyDataSetChanged();
                                } else {
                                    // Remove the message from the list and update the RecyclerView
                                    int index = msgDataArrayList.indexOf(msg);
                                    if (index != -1) {
                                        msgDataArrayList.remove(index);
                                        adapterMsg.notifyItemRemoved(index);
                                    }
                                }
                            }


                        }

                    }
                });
    }

    @Override
    public void onItemClick(int pos) {

        Toast.makeText(this, "Message clicked!", Toast.LENGTH_SHORT).show();

        fs.collection("Petlover").document(email).collection("messages")
                .document(receiver+"")
                .collection("chats").document(pos+"")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                        Intent intent = new Intent(Msg.this, chat.class);
                        intent.putExtra("id", msgDataArrayList.get(pos).id);
                        intent.putExtra("receiver", msgDataArrayList.get(pos).receiver);
                        //intent.putExtra("message", msgDataArrayList.get(pos).message);

                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Msg.this, "No Internet!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onLongItemClick(int pos) {

        AlertDialog.Builder build = new AlertDialog.Builder(Msg.this);
        LayoutInflater inflate = Msg.this.getLayoutInflater();
        View v = inflate.inflate(R.layout.deletemsg, null);

        build.setView(v).setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                /*

                fs.collection("Petlover").document(email)
                        .collection("messages")
                        .document(msgDataArrayList.get(pos).receiver).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Msg.this, "Message deleted!", Toast.LENGTH_SHORT).show();

                                msgDataArrayList.remove(pos);
                                adapterMsg.notifyItemRemoved(pos);
                            }
                        });

                 */

                HashMap map = new HashMap();
                map.put("statusmobile", "DELETED");

                fs.collection("Petlover").document(email)
                        .collection("messages").document(msgDataArrayList.get(pos).receiver).update(map)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                Toast.makeText(Msg.this, "Message deleted!", Toast.LENGTH_SHORT).show();
                                msgDataArrayList.remove(pos);
                                adapterMsg.notifyItemRemoved(pos);
                            }
                        });


            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(Msg.this, "Action cancelled!", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = build.create();
        build.show();



    }


    public void checkMsgDelete()
    {
        fs.collection("Petlover").document(email).collection("messages")
                .whereEqualTo("status", "Deleted")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    }
                    // ...
                });
    }



/*
    public void profIdd()
    {
        fs.collection("Petlover").document(email).collection("messages")
                .document(receiver).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String pp = documentSnapshot.getString("pp");

                        if(pp!=null)
                        {

                            Glide.with(Msg.this).load(pp)
                                    .override(400, 500)
                                    .into(pid);
                        }
                    }
                });
    }

 */

}