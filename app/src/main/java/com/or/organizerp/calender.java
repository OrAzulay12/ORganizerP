package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class calender extends AppCompatActivity {

    private long selectedDateInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        CalendarView calendarView = findViewById(R.id.calendarView);

        // Set the date change listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Store the selected date in milliseconds
            selectedDateInMillis = calendarView.getDate();

            // Display the selected date in a toast for feedback
            Toast.makeText(this, "Selected Date: " + dayOfMonth + "/" + (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
        });

        // When the "Add Event" button is clicked, pass the selected date in milliseconds
        findViewById(R.id.addeventbtn).setOnClickListener(v -> {
            Intent intent = new Intent(calender.this, createnewevent.class);
            intent.putExtra("selectedDate", selectedDateInMillis);  // Pass the date as milliseconds
            startActivity(intent);
        });
    }
}
