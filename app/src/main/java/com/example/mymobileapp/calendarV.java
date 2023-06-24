package com.example.mymobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class calendarV extends AppCompatActivity {

    CalendarView calendar;
    TextView date_view;
    Button sv;
    String Date, name , price, usernm, clue;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_v);



        name = getIntent().getStringExtra("Name");
        price = getIntent().getStringExtra("Price");
        usernm = getIntent().getStringExtra("username");
        clue = getIntent().getStringExtra("clue");




        calendar = (CalendarView)
                findViewById(R.id.calendar);
        date_view = (TextView)
                findViewById(R.id.date_view);
        date_view.setText(Date);
        sv = findViewById(R.id.saveDate);



        calendar
                .setOnDateChangeListener(
                        new CalendarView
                                .OnDateChangeListener() {
                            @Override

                            public void onSelectedDayChange(
                                    @NonNull CalendarView view,
                                    int year,
                                    int month,
                                    int dayOfMonth)
                            {

                                Date
                                        = dayOfMonth + "-"
                                        + (month + 1) + "-" + year;
                                date_view.setText(Date);
                            }
                        });





        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date = date_view.getText().toString();

                Intent intent = new Intent(calendarV.this, serviceDetails.class);
                intent.putExtra("date", date);
                intent.putExtra("Name", name);
                intent.putExtra("Price", price);
                intent.putExtra("username", usernm);
                intent.putExtra("clue", clue);

                startActivity(intent);

            }
        });



    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(calendarV.this, vetclinic.class));
    }
}