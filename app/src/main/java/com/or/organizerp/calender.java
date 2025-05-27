package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0);
            selectedCalendar.set(Calendar.MILLISECOND, 0);
            selected = selectedCalendar.getTime();

            // ✅ Format the date correctly
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            stringDate = sdf.format(selected);

            Toast.makeText(this, "Selected Date: " + stringDate, Toast.LENGTH_SHORT).show();
        });

        btnaddevent.setOnClickListener(v -> {
            if (selected == null) {
                Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            if (selected.before(today.getTime())) {
                Toast.makeText(this, "You cannot create an event in the past.", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(calender.this, createnewevent.class);
                intent.putExtra("selectedDate", stringDate);
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
