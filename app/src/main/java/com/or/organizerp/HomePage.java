package com.or.organizerp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.or.organizerp.adapter.GroupEventAdapter;
import com.or.organizerp.model.GroupEvent;
import com.or.organizerp.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    Button backButton, logOutButton, calenderButton;
    ListView lvAllEvents;

    ArrayList<GroupEvent> events;
    GroupEventAdapter<GroupEvent> eventAdapter;

    private DatabaseService databaseService;

    private GroupEvent selectedEvent;  // Variable to store the selected event for deletion
    private GestureDetector gestureDetector;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        databaseService = DatabaseService.getInstance();
        events = new ArrayList<>();

        // Initialize views
        backButton = findViewById(R.id.btnBackHomePage2);
        calenderButton = findViewById(R.id.btncalender);
        logOutButton = findViewById(R.id.btnLogOutHomePage2);
        lvAllEvents = findViewById(R.id.lvAllEvents);

        eventAdapter = new GroupEventAdapter<>(HomePage.this, 0, 0, events);
        lvAllEvents.setAdapter(eventAdapter);

        // Gesture Detector to detect double taps
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                // Get the position of the event where the double-tap occurred
                int position = lvAllEvents.pointToPosition((int) e.getX(), (int) e.getY());
                if (position != AdapterView.INVALID_POSITION) {
                    selectedEvent = events.get(position);  // Set the selected event

                    // Show a confirmation dialog to delete the event
                    new AlertDialog.Builder(HomePage.this)
                            .setMessage("Do you really want to delete this event?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> deleteEvent())
                            .setNegativeButton("No", null)
                            .show();
                }
                return true; // Indicate that the double tap is handled
            }
        });

        // Fetch events from the database
        databaseService.getGroupEvents(new DatabaseService.DatabaseCallback<List<GroupEvent>>() {
            @Override
            public void onCompleted(List<GroupEvent> object) {
                Log.d("TAG", "onCompleted: " + object);
                events.clear();
                events.addAll(object);
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Log.d("Not ", "onCompleted: " + e.getMessage());
            }
        });

        // Back button
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, MainPage.class);
            startActivity(intent);
        });

        // Log out functionality
        logOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomePage.this, MainPage.class);
            startActivity(intent);
        });

        // Navigate to Calendar
        calenderButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, calender.class);
            startActivity(intent);
        });

        // Set item click listener to show event details on single tap
        lvAllEvents.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedEvent = events.get(position);  // Set the selected event

                // Navigate to Event Detail page
                Intent intent = new Intent(HomePage.this, EventDetailActivity.class); // Your event detail activity
                intent.putExtra("eventId", selectedEvent.getId());  // Pass eventId to view details
                startActivity(intent);
            }
        });
    }

    // Method to delete the event from the database
    private void deleteEvent() {
        if (selectedEvent != null) {
            String eventId = selectedEvent.getId();  // Get the selected event ID

            // Call delete method from DatabaseService
            databaseService.deleteGroupEvent(eventId, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(HomePage.this, "Event deleted successfully.", Toast.LENGTH_SHORT).show();
                    events.remove(selectedEvent);  // Remove event from list
                    eventAdapter.notifyDataSetChanged();  // Refresh list view
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e("HomePage", "Failed to delete event", e);
                    Toast.makeText(HomePage.this, "Failed to delete event.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Override onTouchEvent to detect double tap
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
