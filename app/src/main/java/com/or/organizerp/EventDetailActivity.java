package com.or.organizerp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.or.organizerp.adapter.UserNamAdapter;
import com.or.organizerp.model.GroupEvent;
import com.or.organizerp.model.User;
import com.or.organizerp.services.AuthenticationService;
import com.or.organizerp.services.DatabaseService;

import java.util.ArrayList;

public class EventDetailActivity extends AppCompatActivity {

    private GroupEvent selectedEvent;  // To store the selected event
    private DatabaseService databaseService;
    private GestureDetector gestureDetector;  // For detecting double-tap gestures
    Button backHomePageButton;
    private TextView eventTitle,eventDescription,eventDate,eventKind, eventTime;
    private AuthenticationService authenticationService;

    String uid="";
    Intent takeit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_detail);

        // Initialize the DatabaseService instance
        databaseService = DatabaseService.getInstance();

        backHomePageButton = findViewById(R.id.backtohomepagebtn2);

        authenticationService = AuthenticationService.getInstance();
        uid = authenticationService.getCurrentUserId();
        takeit=getIntent();
        selectedEvent= (GroupEvent) takeit.getSerializableExtra("event");


        // Retrieve the event ID passed from the previous activity
        if(selectedEvent!=null) {



            // Fetch the event details from the database using the event ID
            loadEventDetails();

        }

        // Set up the GestureDetector for double tap
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                showDeleteConfirmationDialog();  // Show delete confirmation dialog on double tap
                return true; // Indicate the gesture is handled
            }
        });

        backHomePageButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailActivity.this, HomePage.class);
            startActivity(intent);
        });

        // Set up the onApplyWindowInsetsListener for handling window insets (like system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadEventDetails() {



        // Assuming you have TextViews for event title, description, date, and kind of event
        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        eventDate = findViewById(R.id.eventDate);
        eventKind = findViewById(R.id.eventKind);  // New TextView for event type
        eventTime=findViewById(R.id.eventTime);

        if (selectedEvent != null) {
            eventTitle.setText(selectedEvent.getName());
            eventDescription.setText(selectedEvent.getDetails());
            eventDate.setText(selectedEvent.getDate());
            eventKind.setText(selectedEvent.getType());
            eventTime.setText(selectedEvent.getTime());

        } else {
            Toast.makeText(EventDetailActivity.this, "Event details not available", Toast.LENGTH_SHORT).show();
        }
    }




    private void showDeleteConfirmationDialog() {
        // Show a confirmation dialog when the user double taps
        new AlertDialog.Builder(EventDetailActivity.this)
                .setMessage("Do you really want to delete this event?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> deleteEvent(uid,selectedEvent))
                .setNegativeButton("No", null)
                .show();
    }

    // Method to delete the event from the database
    private void deleteEvent(String uid, GroupEvent selectedEvent) {
        if (selectedEvent != null) {


            databaseService.deleteEventForUser(selectedEvent,  uid,new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(EventDetailActivity.this, "Event deleted successfully.", Toast.LENGTH_SHORT).show();




                    Intent intent = new Intent(EventDetailActivity.this, HomePage.class);
                    startActivity(intent);

                }

                @Override
                public void onFailed(Exception e) {
                    Log.e("EventDetailActivity", "Failed to delete event", e);
                    Toast.makeText(EventDetailActivity.this, "Failed to delete event.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        else {
            Log.d("EventDetailActivity", selectedEvent.toString()+"");
            Toast.makeText(EventDetailActivity.this, selectedEvent.toString()+"  "+uid, Toast.LENGTH_SHORT).show();
        }
    }


    // Override onTouchEvent to detect gestures (including double tap)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}