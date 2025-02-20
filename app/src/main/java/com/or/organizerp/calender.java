package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class calender extends AppCompatActivity {
    Button addeventButton, backtohomepageButton;
    long selectedDateInMillis;  // Store selected date as milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        // Initialize the buttons and calendar view
        addeventButton = findViewById(R.id.addeventbtn);
        backtohomepageButton = findViewById(R.id.backtohomepagebtn);
        CalendarView calendarView = findViewById(R.id.calendarView);
        selectedDateInMillis = calendarView.getDate();  // Set initial date in milliseconds

        // Set the listener for the calendar view to capture the selected date
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Save the date in milliseconds for passing to the next activity
            selectedDateInMillis = calendarView.getDate();  // Get date in milliseconds

            // Display the selected date in a toast for feedback
            Toast.makeText(this, "Selected Date: " + dayOfMonth + "/" + (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
        });

        // When the "Add Event" button is clicked, navigate to create new event activity
        addeventButton.setOnClickListener(v -> {
            // Pass the selected date (in milliseconds) to the next activity
            Intent intent = new Intent(calender.this, createnewevent.class);
            intent.putExtra("selectedDate", selectedDateInMillis);  // Pass the date in milliseconds
            startActivity(intent);
        });

        // When the "Back to Homepage" button is clicked, navigate to HomePage activity
        backtohomepageButton.setOnClickListener(v -> {
            Intent intent = new Intent(calender.this, HomePage.class);
            startActivity(intent);
        });
    }
}
