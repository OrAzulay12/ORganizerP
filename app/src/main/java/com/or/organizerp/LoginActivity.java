package com.or.organizerp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.or.organizerp.model.User;
import com.or.organizerp.services.AuthenticationService;
import com.or.organizerp.services.DatabaseService;
import com.or.organizerp.utils.SharedPreferencesUtil;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginButton, backButton;


    String admin = "idosshati@gmail.com";
    public static Boolean isAdmin=false;


private static final String TAG = "loginToFireBase";
    TextView tvLog;
    EditText etEmail2, etPass2;
    Button btnLog;

    String email2, pass2;




    private AuthenticationService authenticationService;
    private DatabaseService databaseService;
    private User user;



    private FirebaseDatabase database;
    private DatabaseReference myRef, farmerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);





        init_views();

        /// get the instance of the authentication service
        authenticationService = AuthenticationService.getInstance();
        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();
       user= SharedPreferencesUtil.getUser(this);


     if(user!=null) {
         email2 = user.getEmail();
         pass2 = user.getPassword();
         etEmail2.setText(email2);
         etPass2.setText(pass2);

     }
        btnLog.setOnClickListener(this);


    }

    private void init_views() {
        btnLog = findViewById(R.id.btnGologin);
        etEmail2 = findViewById(R.id.etemailLogIn);
        etPass2 = findViewById(R.id.etPasswordLogIn);
        backButton = findViewById(R.id.btnBackLogin);

        backButton.setOnClickListener(v -> {
            // Go back to the previous screen (e.g., Register activity)
            Intent intent = new Intent(LoginActivity.this, MainPage.class);
            startActivity(intent);
        });

    }


    @Override
    public void onClick(View v) {
        email2 = etEmail2.getText().toString();
        pass2 = etPass2.getText().toString();

        loginUser(email2,pass2);



    }



private void loginUser(String email, String password) {
        authenticationService.signIn(email, password, new AuthenticationService.AuthCallback<String>() {
/// Callback method called when the operation is completed
/// @param uid the user ID of the user that is logged in
@Override
public void onCompleted(String uid) {
        Log.d(TAG, "onCompleted: User logged in successfully");
        /// get the user data from the database


        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
        @Override
            public void onCompleted(User u) {
                    user = u;
                Log.d(TAG, "onCompleted: User data retrieved successfully");
                /// save the user data to shared preferences
                   SharedPreferencesUtil.saveUser(LoginActivity.this, user);
               /// Redirect to main activity and clear back stack to prevent user from going back to login screen
               Intent mainIntent = new Intent(LoginActivity.this, HomePage  .class);
                /// Clear the back stack (clear history) and start the MainActivity
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);

        }

                    @Override
public void onFailed(Exception e) {
        Log.e(TAG, "onFailed: Failed to retrieve user data", e);
        /// Show error message to user
        etEmail2.setError("Invalid email or password");
        etEmail2.requestFocus();
        /// Sign out the user if failed to retrieve user data
        /// This is to prevent the user from being logged in again
        authenticationService.signOut();

        }
        });


        }



@Override
public void onFailed(Exception e) {
        Log.e(TAG, "onFailed: Failed to log in user", e);
        /// Show error message to user
        etPass2.setError("Invalid email or password");
        etPass2.requestFocus();

        }
        });
        }




        }
