package com.or.organizerp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.or.organizerp.model.GroupEvent;
import com.or.organizerp.services.DatabaseService;

public class EventDetailActivity extends AppCompatActivity {

    private GroupEvent selectedEvent;  // To store the selected event
    private DatabaseService databaseService;
    private GestureDetector gestureDetector;  // For detecting double-tap gestures
    Button backHomePageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_detail);

        backHomePageButton = findViewById(R.id.backtohomepagebtn2);

        // Initialize the DatabaseService instance
        databaseService = DatabaseService.getInstance();

        // Retrieve the event ID passed from the previous activity
        String eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            // Fetch the event details from the database using the event ID
            loadEventDetails(eventId);
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

    private void loadEventDetails(String eventId) {
        databaseService.getGroupEventById(eventId, new DatabaseService.DatabaseCallback<GroupEvent>() {
            @Override
            public void onCompleted(GroupEvent event) {
                selectedEvent = event;

                // Assuming you have TextViews for event title, description, date, and kind of event
                TextView eventTitle = findViewById(R.id.eventTitle);
                TextView eventDescription = findViewById(R.id.eventDescription);
                TextView eventDate = findViewById(R.id.eventDate);
                TextView eventKind = findViewById(R.id.eventKind);  // New TextView for event type

                if (selectedEvent != null) {
                    eventTitle.setText(selectedEvent.getName());
                    eventDescription.setText(selectedEvent.getDetails());
                    eventDate.setText(selectedEvent.getDate());
                    eventKind.setText(selectedEvent.getType());
                } else {
                    Toast.makeText(EventDetailActivity.this, "Event details not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(EventDetailActivity.this, "Failed to load event details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        // Show a confirmation dialog when the user double taps
        new AlertDialog.Builder(EventDetailActivity.this)
                .setMessage("Do you really want to delete this event?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> deleteEvent())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteEvent() {
        if (selectedEvent != null) {
            String eventId = selectedEvent.getId();  // Get the event ID
            databaseService.deleteGroupEvent(eventId, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    // Event successfully deleted, show success message and finish the activity
                    Toast.makeText(EventDetailActivity.this, "Event deleted successfully.", Toast.LENGTH_SHORT).show();
                    finish();  // Close the activity after deleting the event
                }

                @Override
                public void onFailed(Exception e) {
                    // Handle failure during event deletion
                    Toast.makeText(EventDetailActivity.this, "Failed to delete event.", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    // Override onTouchEvent to detect gestures (including double tap)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
