package com.or.organizerp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.or.organizerp.model.User;

public class Register extends AppCompatActivity {

    EditText etName, etPhone, etEmail, etPassword;
    Button saveButton, backButton;

    private DatabaseReference myRef;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Apply window insets for padding adjustments (system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the EditText fields and Save button
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        saveButton = findViewById(R.id.btnSave);
        backButton = findViewById(R.id.btnBack);  // Initialize back button
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        mAuth = FirebaseAuth.getInstance();

        // Back Button functionality
        backButton.setOnClickListener(v -> {
            // Go back to the previous screen (e.g., Login activity)
            Intent intent = new Intent(Register.this, MainPage.class); // Replace with your login activity
            startActivity(intent);
            finish(); // Optional, to close Register activity after navigating
        });

        // Set the OnClickListener for the Save button
        saveButton.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validate inputs
            if (validateInput(name, phone, email, password)) {
                // Create a User object (assuming you have a User class)
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "createUserWithEmail:success");
                                    FirebaseUser fireuser = mAuth.getCurrentUser();
                                    User newUser = new User(mAuth.getUid(), name, phone, email, password);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("email", email);
                                    editor.apply();  // Use apply() for asynchronous saving

                                    // Display success message
                                    Toast.makeText(Register.this, "Data Saved! Welcome, " + newUser.getName(), Toast.LENGTH_LONG).show();

                                    // Redirect to Home or other activity
                                    Intent goToHome = new Intent(Register.this, MainPage.class);  // Change HomeActivity to your target activity
                                    startActivity(goToHome);
                                    finish(); // Optional
                                } else {
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    // Validate the user input fields
    private boolean validateInput(String name, String phone, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return false;
        }
        return true;
    }
}
