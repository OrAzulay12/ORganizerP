package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    Button loginButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Apply window insets for padding adjustments (system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons
        loginButton = findViewById(R.id.btnlogin);  // Initialize the login button
        backButton = findViewById(R.id.btnBack);    // Initialize the back button

        // Set onClickListener for backButton
        backButton.setOnClickListener(v -> {
            // Go back to the previous screen (e.g., Register activity)
            Intent intent = new Intent(LoginActivity.this, Register.class);
            startActivity(intent);
        });

        // Set onClickListener for loginButton
        loginButton.setOnClickListener(v -> {
            // Implement login logic (e.g., Firebase authentication)
            Intent intent = new Intent(LoginActivity.this, MainPage.class);
            startActivity(intent);
        });
    }
}
