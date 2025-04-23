package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class calender extends AppCompatActivity implements View.OnClickListener {


    private Date selected;
    private String stringDate;
    Button btnaddevent, btnbacktohomepage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        btnbacktohomepage=  findViewById(R.id.backtohomepagebtn);

        btnbacktohomepage.setOnClickListener(this);
        CalendarView calendarView = findViewById(R.id.calendarView);

        btnaddevent=  findViewById(R.id.addeventbtn);
        btnaddevent.setOnClickListener(this);
        // Set the date change listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Store the selected date in milliseconds
            selected  = new Date(year,month+1,dayOfMonth);

            stringDate=dayOfMonth + "/" + (month + 1) + "/" + year;

            // Display the selected date in a toast for feedback
            Toast.makeText(this, "Selected Date: " + dayOfMonth + "/" + (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, stringDate,Toast.LENGTH_LONG).show();
        });

        // When the "Add Event" button is clicked, pass the selected date in milliseconds
        findViewById(R.id.addeventbtn).setOnClickListener(v -> {
            Intent intent = new Intent(calender.this, createnewevent.class);
            intent.putExtra("selectedDate", stringDate);  // Pass the date as milliseconds
            startActivity(intent);
        });




    }

    @Override
    public void onClick(View v) {

        if(v==btnbacktohomepage){
            Intent intent = new Intent(calender.this, HomePage.class);

            startActivity(intent);


        }

    }

}