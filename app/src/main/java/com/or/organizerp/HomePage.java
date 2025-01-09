package com.or.organizerp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {

    Button backButton, logOutButton;
    Spinner spCreateEvent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);  // Make sure this layout exists

        // Initialize backButton and spinner
        backButton = findViewById(R.id.btnBackHomePage2); // Make sure this ID exists in your layout
        logOutButton = findViewById(R.id.btnLogOutHomePage2); // Make sure this ID exists in your layout
        spCreateEvent = findViewById(R.id.spCreateEvent2); // Make sure this ID exists in your layout

        // Set onClickListener for backButton
        backButton.setOnClickListener(v -> {
            // Go back to the previous screen (MainPage activity)
            Intent intent = new Intent(HomePage.this, MainPage.class);
            startActivity(intent);
        });

        // Log out functionality
        logOutButton.setOnClickListener(v -> {
            // Sign out the user from Firebase
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomePage.this, MainPage.class);
            startActivity(intent);
        });

        // Load the string-array from resources
        String[] eventTypes = getResources().getStringArray(R.array.ArryEventType);  // Ensure this array exists in strings.xml

        // Create an ArrayAdapter using the eventTypes array
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
     //   spCreateEvent.setAdapter(adapter);
    }
}
