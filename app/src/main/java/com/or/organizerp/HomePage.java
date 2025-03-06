package com.or.organizerp;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.or.organizerp.adapter.GroupEventAdapter;
import com.or.organizerp.model.GroupEvent;
import com.or.organizerp.services.AuthenticationService;
import com.or.organizerp.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    Button logOutButton, calenderButton;
    ListView lvAllEvents;

    ArrayList<GroupEvent> events;
    GroupEventAdapter<GroupEvent> eventAdapter;

    private DatabaseService databaseService;
    AuthenticationService authenticationService;
    String id;

    private GroupEvent selectedEvent;
    private GestureDetector gestureDetector;

    // Activity Result Launcher to handle event deletion response
    private final ActivityResultLauncher<Intent> eventDetailLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String deletedEventId = result.getData().getStringExtra("deletedEventId");
                    if (deletedEventId != null) {
                        removeEventFromList(deletedEventId);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        authenticationService = AuthenticationService.getInstance();
        id = authenticationService.getCurrentUserId();

        databaseService = DatabaseService.getInstance();
        events = new ArrayList<>();

        // Initialize views

        calenderButton = findViewById(R.id.btncalender);
        logOutButton = findViewById(R.id.btnLogOutHomePage2);
        lvAllEvents = findViewById(R.id.lvAllEvents);

        eventAdapter = new GroupEventAdapter<>(HomePage.this, 0, 0, events);
        lvAllEvents.setAdapter(eventAdapter);

        // Gesture Detector to detect double taps
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                int position = lvAllEvents.pointToPosition((int) e.getX(), (int) e.getY());
                if (position != AdapterView.INVALID_POSITION) {
                    selectedEvent = events.get(position);

                    new AlertDialog.Builder(HomePage.this)
                            .setMessage("Do you really want to delete this event?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> deleteEvent())
                            .setNegativeButton("No", null)
                            .show();
                }
                return true;
            }
        });

        // Fetch events from the database
        databaseService.getUserEvents(id, new DatabaseService.DatabaseCallback<List<GroupEvent>>() {
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
        lvAllEvents.setOnItemClickListener((parent, view, position, id) -> {
            selectedEvent = events.get(position);
            Intent intent = new Intent(HomePage.this, EventDetailActivity.class);
            intent.putExtra("event", selectedEvent);
            startActivity(intent); // Use ActivityResultLauncher
        });
    }

    // Method to delete the event from the database
    private void deleteEvent() {
        if (selectedEvent != null) {
            String eventId = selectedEvent.getId();

            databaseService.deleteEventForUser(selectedEvent,  id,new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(HomePage.this, "Event deleted successfully.", Toast.LENGTH_SHORT).show();
                    removeEventFromList(eventId);
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e("HomePage", "Failed to delete event", e);
                    Toast.makeText(HomePage.this, "Failed to delete event.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Method to remove deleted event from the list
    private void removeEventFromList(String eventId) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(eventId)) {
                events.remove(i);
                eventAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    // Override onTouchEvent to detect double tap
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
