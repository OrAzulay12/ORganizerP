package com.or.organizerp;

import android.content.Context;
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
import com.or.organizerp.services.AuthenticationService;
import com.or.organizerp.services.DatabaseService;
import com.or.organizerp.utils.SharedPreferencesUtil;

public class Register extends AppCompatActivity {

    EditText etFName,etLName, etPhone, etEmail, etPassword;
    Button saveButton, backButton;

    private static final String TAG = "RegisterActivity";


    private AuthenticationService authenticationService;
    private DatabaseService databaseService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        /// get the instance of the authentication service
        authenticationService = AuthenticationService.getInstance();
        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();


        // Apply window insets for padding adjustments (system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the EditText fields and Save button
        etFName = findViewById(R.id.etFname);
        etLName = findViewById(R.id.etLName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        saveButton = findViewById(R.id.btnSave);
        backButton = findViewById(R.id.btnBack);  // Initialize back button
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        // Back Button functionality
        backButton.setOnClickListener(v -> {
            // Go back to the previous screen (e.g., Login activity)
            Intent intent = new Intent(Register.this, MainPage.class); // Replace with your login activity
            startActivity(intent);
            finish(); // Optional, to close Register activity after navigating
        });

        // Set the OnClickListener for the Save button
        saveButton.setOnClickListener(v -> {
            String fname = etFName.getText().toString().trim();
            String lname = etLName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validate inputs
            if (validateInput(fname, lname, phone, email, password)) {

                /// Register user
                registerUser(fname, lname, phone, email, password);
            }
        });
    }

            // Create a User object (assuming you have a User class)


                /// Register the user
                private void registerUser(String fname, String lname, String  phone, String email, String password) {
                    Log.d(TAG, "registerUser: Registering user...");

                    /// call the sign up method of the authentication service
                    authenticationService.signUp(email, password, new AuthenticationService.AuthCallback<String>() {

                        @Override
                        public void onCompleted(String uid) {
                            Log.d(TAG, "onCompleted: User registered successfully");
                            /// create a new user object
                            User user = new User();
                            user.setId(uid);
                            user.setEmail(email);
                            user.setPassword(password);
                            user.setFname(fname);
                            user.setLname(lname);
                            user.setPhone(phone);

                            /// call the createNewUser method of the database service
                            databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {

                                @Override
                                public void onCompleted(Void object) {
                                    Log.d(TAG, "onCompleted: User registered successfully");
                                    /// save the user to shared preferences
                                    SharedPreferencesUtil.saveUser(Register.this, user);
                                    Log.d(TAG, "onCompleted: Redirecting to MainActivity");
                                    /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                                    Intent mainIntent = new Intent(Register.this, HomePage.class);
                                    /// clear the back stack (clear history) and start the MainActivity
                                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                }

                                @Override
                                public void onFailed(Exception e) {
                                    Log.e(TAG, "onFailed: Failed to register user", e);
                                    /// show error message to user
                                    Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                                    /// sign out the user if failed to register
                                    /// this is to prevent the user from being logged in again
                                    authenticationService.signOut();
                                }
                            });
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Log.e(TAG, "onFailed: Failed to register user", e);
                            /// show error message to user
                            Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                        }
                    });


                }








    // Validate the user input fields
    private boolean validateInput(String fname,   String lname ,String phone, String email, String password) {
        if (TextUtils.isEmpty(fname)) {
            etFName.setError("Name is required");
            return false;
        }

        if (TextUtils.isEmpty(lname)) {
            etLName.setError("Name is required");
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
