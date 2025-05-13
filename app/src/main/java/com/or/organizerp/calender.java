package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class calender extends AppCompatActivity implements View.OnClickListener {

    private Date selected;
    private String stringDate;
    Button btnaddevent, btnbacktohomepage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        btnbacktohomepage = findViewById(R.id.backtohomepagebtn);
        btnaddevent = findViewById(R.id.addeventbtn);
        CalendarView calendarView = findViewById(R.id.calendarView);

        btnbacktohomepage.setOnClickListener(this);

        // Set the date change listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Note: Month is 0-based in Calendar
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0);
            selectedCalendar.set(Calendar.MILLISECOND, 0);
            selected = selectedCalendar.getTime();

            stringDate = dayOfMonth + "/" + (month + 1) + "/" + year;

            // Display the selected date in a toast
            Toast.makeText(this, "Selected Date: " + stringDate, Toast.LENGTH_SHORT).show();
        });

        // Handle Add Event button click
        btnaddevent.setOnClickListener(v -> {
            if (selected == null) {
                Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get today's date with time cleared
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // Compare selected date with today's date
            if (selected.before(today.getTime())) {
                Toast.makeText(this, "You cannot create an event in the past.", Toast.LENGTH_LONG).show();
            } else {

                String zero = "", zero2 = "";

                if ((selected.getMonth() + 1) < 10) zero = "0";  else zero="" ;
                if ((selected.getDate()) < 10) zero2 = "0"  ;  else zero2="";


              String  selectedDate2 = zero2 + selected.getDay()+"/" + zero + (selected.getMonth() + 1) + "/" + selected.getYear();



                Intent intent = new Intent(calender.this, createnewevent.class);
                intent.putExtra("selectedDate", selectedDate2);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnbacktohomepage) {
            Intent intent = new Intent(calender.this, HomePage.class);
            startActivity(intent);
        }
    }
}
