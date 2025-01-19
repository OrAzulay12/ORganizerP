package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class calender extends AppCompatActivity {
    Button addeventButton, backtohomepageButton;
    public static String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        addeventButton = findViewById(R.id.addeventbtn);
        backtohomepageButton = findViewById(R.id.backtohomepagebtn);

        CalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
             date = dayOfMonth + "/" + (month + 1) + "/" + year;
            Toast.makeText(this, "Selected Date: " + date, Toast.LENGTH_SHORT).show();
        });

        addeventButton.setOnClickListener(v -> {
            Intent intent = new Intent(calender.this, createnewevent.class);
            startActivity(intent);
        });

        backtohomepageButton.setOnClickListener(v -> {
            Intent intent = new Intent(calender.this, HomePage.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
