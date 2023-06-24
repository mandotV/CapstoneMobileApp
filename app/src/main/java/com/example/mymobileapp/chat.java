package com.example.mymobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class chat extends AppCompatActivity {

    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    //Timestamp serverT = new Timestamp(new Date());

    EditText sendTomsg;//
    String rec;
    String email;
    String count;

    ImageView chatpp;


    RecyclerView recyclerView;
    ArrayList<chatData> chatDataArrayList;
    adapterChat adapterChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ImageView back = findViewById(R.id.btn_back);
        ImageView send = findViewById(R.id.btn_send);
        chatpp = findViewById(R.id.chat_profile);
        TextView chat_name = findViewById(R.id.chat_name);
        sendTomsg = findViewById(R.id.sendTomsg);

        email = firebaseUser.getEmail();
        rec = getIntent().getStringExtra("receiver");
        count = getIntent().getStringExtra("id");

        chat_name.setText(rec);

        recyclerView = findViewById(R.id.recycler_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatDataArrayList = new ArrayList<chatData>();
        adapterChat = new adapterChat(chat.this, chatDataArrayList);

        recyclerView.setAdapter(adapterChat);



        EventChangeListener();
        profileCheck();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fs.collection("Petlover").document(email).collection("messages")
                        .document(rec + "")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                String new_msg = sendTomsg.getText().toString();

                                    Long tm = System.currentTimeMillis();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
                                    Date date = new Date(tm);
                                    String tm2 = simpleDateFormat.format(date);

                            /*
                                    HashMap hash2 = new HashMap();
                                    hash2.put("message", new_msg);
                                    hash2.put("receiver", rec);
                                    hash2.put("id", email);



                                fs.collection("Petlover").document(email).collection("messages")
                                        .document(rec + "").set(hash2)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });

                             */

                                    HashMap hash = new HashMap();
                                    hash.put("id",email);
                                    hash.put("sender", email);
                                    hash.put("receiver", rec);
                                    hash.put("message", new_msg);
                                    hash.put("timestamp", tm2);

                                    fs.collection("Petlover").document(email).collection("messages")
                                            .document(rec+"")
                                            .collection("chats").document()
                                            .set(hash)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {


                                                }
                                            });

                                    sendTomsg.getText().clear();
                            }
                        });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(chat.this, Msg.class));

            }
        });
    }


    public void EventChangeListener() {

         fs.collection("Petlover").document(email).collection("messages")
                 .document(rec + "")
                 .collection("chats").orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error !=null){
                            Log.e("Error",error.getMessage());
                            return;
                        }

                        if(value != null) {


                            for (DocumentChange dc : value.getDocumentChanges()) {

                                int newMsgPosition = chatDataArrayList.size() - 1;

                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    chatDataArrayList.add(dc.getDocument().toObject(chatData.class));
                                    recyclerView.scrollToPosition(newMsgPosition);
                                }

                                adapterChat.notifyDataSetChanged();


                            }
                        }

                    }
                });
    }


    public void profileCheck(){

        fs.collection("Petlover").document(email).collection("messages")
                .document(rec).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String pp = documentSnapshot.getString("pp");

                        Glide.with(chat.this).load(pp)
                                .override(400, 500)
                                .into(chatpp);
                    }
                });

    }
}