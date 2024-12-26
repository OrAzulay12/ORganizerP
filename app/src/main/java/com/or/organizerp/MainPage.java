package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

public class MainPage extends AppCompatActivity {

    Button loginButton, registerButton, odotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);



        // Initialize buttons
        loginButton = findViewById(R.id.btntologin);
        registerButton = findViewById(R.id.btntoregister);
        odotButton = findViewById(R.id.btntoodot);

        // Handle window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set click listeners for buttons
        loginButton.setOnClickListener(v -> openLoginPage());
        registerButton.setOnClickListener(v -> openRegisterPage());
        odotButton.setOnClickListener(v -> openAboutPage());
    }

    // Navigate to the Login Page (ensure LoginActivity is correctly referenced)
    private void openLoginPage() {
        Intent loginIntent = new Intent(MainPage.this, LoginActivity.class);  // Ensure the correct class name
        startActivity(loginIntent);
    }

    // Navigate to the Register Page
    private void openRegisterPage() {
        Intent registerIntent = new Intent(MainPage.this, Register.class);
        startActivity(registerIntent);
    }

    // Navigate to the About Page
    private void openAboutPage() {
        Intent aboutIntent = new Intent(MainPage.this, About.class);
        startActivity(aboutIntent);
    }
}
