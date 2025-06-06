package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

public class EventDetailActivity extends AppCompatActivity {

    private GroupEvent selectedEvent;
    private DatabaseService databaseService;
    private GestureDetector gestureDetector;
    private ListView lvShowMembersInGroup;
    private TextView eventTitle, eventDescription, eventDate, eventKind, eventTime;
    private AuthenticationService authenticationService;
    private Button backHomePageButton, gotoediteventButton;
    private String uid = "";
    private Intent takeit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();
        authenticationService = AuthenticationService.getInstance();
        uid = authenticationService.getCurrentUserId();
        takeit = getIntent();
        selectedEvent = (GroupEvent) takeit.getSerializableExtra("event");

        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        eventDate = findViewById(R.id.eventDate);
        eventKind = findViewById(R.id.eventKind);
        eventTime = findViewById(R.id.eventTime);
        lvShowMembersInGroup = findViewById(R.id.lvShowMembersInGroup);
        backHomePageButton = findViewById(R.id.btnbacktohomepagebtn2);
        gotoediteventButton = findViewById(R.id.btnEditEvent);

        if (!uid.equals(selectedEvent.getAdmin().getId())) {
            gotoediteventButton.setVisibility(View.INVISIBLE);
        }

        if (selectedEvent != null) {
            loadEventDetails();
            loadEventMembers();
        }

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                showDeleteConfirmationDialog();
                return true;
            }
        });

        backHomePageButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailActivity.this, HomePage.class);
            startActivity(intent);
        });

        gotoediteventButton.setOnClickListener(v -> {
            if (uid.equals(selectedEvent.getAdmin().getId())) {
                Intent intent = new Intent(EventDetailActivity.this, EditExistingEvent.class);
                intent.putExtra("event", selectedEvent);
                startActivity(intent);
            }
        });
    }

    private void loadEventDetails() {
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

    private void loadEventMembers() {
        databaseService.getEventMembers(selectedEvent.getId(), new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> users) {
                if (users != null && !users.isEmpty()) {
                    UserNamAdapter adapter = new UserNamAdapter(EventDetailActivity.this, R.layout.username, R.id.tvfullName, users);
                    lvShowMembersInGroup.setAdapter(adapter);
                } else {
                    Toast.makeText(EventDetailActivity.this, "No members in this event.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("EventDetailActivity", "Failed to load event members", e);
                Toast.makeText(EventDetailActivity.this, "Failed to load members.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(EventDetailActivity.this)
                .setMessage("Do you really want to delete this event?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> deleteEvent(uid, selectedEvent))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteEvent(String uid, GroupEvent selectedEvent) {
        if (selectedEvent != null) {
            // First, delete the event for all users
            databaseService.getEventMembers(selectedEvent.getId(), new DatabaseService.DatabaseCallback<List<User>>() {
                @Override
                public void onCompleted(List<User> users) {
                    if (users != null) {
                        for (User user : users) {
                            // Assuming a method to delete an event for each user exists
                            databaseService.deleteEventForUser(selectedEvent, user.getId(), new DatabaseService.DatabaseCallback<Void>() {
                                @Override
                                public void onCompleted(Void object) {
                                    // Optionally, track how many users' events have been deleted
                                }

                                @Override
                                public void onFailed(Exception e) {
                                    Log.e("EventDetailActivity", "Failed to delete event for user: " + user.getId(), e);
                                }
                            });
                        }
                    }
                    // After deleting event for all users, delete the event from the creator's account
                    databaseService.deleteEventForUser(selectedEvent, uid, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            Toast.makeText(EventDetailActivity.this, "Event deleted successfully for all users.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EventDetailActivity.this, HomePage.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Log.e("EventDetailActivity", "Failed to delete event for creator", e);
                            Toast.makeText(EventDetailActivity.this, "Failed to delete event for creator.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e("EventDetailActivity", "Failed to load event members", e);
                    Toast.makeText(EventDetailActivity.this, "Failed to load members.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d("EventDetailActivity", "Event data is null");
            Toast.makeText(EventDetailActivity.this, "Event data is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
