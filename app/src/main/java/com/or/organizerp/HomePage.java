package com.or.organizerp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.or.organizerp.adapter.GroupEventAdapter;
import com.or.organizerp.model.GroupEvent;
import com.or.organizerp.services.DatabaseService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomePage extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Button backButton, logOutButton, calenderButton, deleteEventButton;
    TextView txtEventDetails;

    ListView lvAllEvents;

    ArrayList<GroupEvent> events;

    GroupEventAdapter<GroupEvent> eventAdapter;

    private DatabaseService databaseService;

    private GroupEvent selectedEvent;  // Variable to store the selected event for deletion

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        databaseService = DatabaseService.getInstance();
        events = new ArrayList<>();

        // Initialize views
        txtEventDetails = findViewById(R.id.txtEventDetails);
        backButton = findViewById(R.id.btnBackHomePage2);
        calenderButton = findViewById(R.id.btncalender);
        logOutButton = findViewById(R.id.btnLogOutHomePage2);
        deleteEventButton = findViewById(R.id.btnDeleteEvent);

        lvAllEvents = findViewById(R.id.lvAllEvents);

        eventAdapter = new GroupEventAdapter<>(HomePage.this, 0, 0, events);
        lvAllEvents.setAdapter(eventAdapter);
        lvAllEvents.setOnItemClickListener(this);

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

        // Delete event from the database
        deleteEventButton.setOnClickListener(v -> {
            if (selectedEvent == null) {
                Toast.makeText(HomePage.this, "No event selected to delete.", Toast.LENGTH_SHORT).show();
                return;
            }
            lvAllEvents.setOnItemClickListener((parent, view, position, id) -> {
                selectedEvent = events.get(position);
            });

            String eventId = selectedEvent.getId();

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
        });

// Using Long to get the event date

//        if (eventName != null && eventDescription != null && eventType != null && eventDateMillis > 0) {
////            String eventDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(eventDateMillis));
////            String eventDetails = "Event: " + eventName + "\nDescription: " + eventDescription + "\nType: " + eventType + "\nDate: " + eventDate;
////            txtEventDetails.setText(eventDetails);
////        } else {
////            txtEventDetails.setText("No event created yet.");
////        }
////
//         List item click listener to capture the selected event
////        lvAllEvents.setOnItemClickListener((parent, view, position, id) -> {
////            selectedEvent = events.get(position);
////        });
////    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        // Handle item click if necessary (currently handled by OnItemClickListener for lvAllEvents)
   }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
