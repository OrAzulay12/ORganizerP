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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginButton, backButton;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    String admin = "idosshati@gmail.com";
    public static Boolean isAdmin=false;





    private static final String TAG = "loginToFireBase";
    TextView tvLog;
    EditText etEmail2, etPass2;
    Button btnLog;

    String email2, pass2;
    private FirebaseAuth mAuth;








    private FirebaseDatabase database;
    private DatabaseReference myRef, farmerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();



        init_views();

        database = FirebaseDatabase.getInstance();
        email2 = sharedpreferences.getString("email", "");
        pass2 = sharedpreferences.getString("password", "");
        etEmail2.setText(email2);
        etPass2.setText(pass2);
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

        mAuth.signInWithEmailAndPassword(email2, pass2)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putString("email", email2);
                            editor.putString("password", pass2);

                            editor.commit();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();

                            myRef = database.getReference("Users").child(mAuth.getUid());


                        //    if (email2.equals(admin)) {
                        //        Intent goLog = new Intent(getApplicationContext(), AdminPage.class);
                       //         startActivity(goLog);



                       //     }


                            Intent intent = new Intent(LoginActivity.this, HomePage.class);
                            startActivity(intent);



                        }

                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }

                });
    }





}
