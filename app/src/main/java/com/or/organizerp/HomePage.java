package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.or.organizerp.adapter.GroupEventAdapter;
import com.or.organizerp.model.GroupEvent;
import com.or.organizerp.services.AuthenticationService;
import com.or.organizerp.services.DatabaseService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomePage extends AppCompatActivity {

    ListView lvAllEvents;
    ListView lvOldEvents;

    ArrayList<GroupEvent> events;       // upcoming & today events (all events with date today or later)
    ArrayList<GroupEvent> oldEvents;    // past events (date before today)

    GroupEventAdapter<GroupEvent> eventAdapter;
    GroupEventAdapter<GroupEvent> oldEventAdapter;

    private DatabaseService databaseService;
    AuthenticationService authenticationService;
    String id;

    private GroupEvent selectedEvent;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        authenticationService = AuthenticationService.getInstance();
        id = authenticationService.getCurrentUserId();

        databaseService = DatabaseService.getInstance();

        events = new ArrayList<>();
        oldEvents = new ArrayList<>();

        initViews();

        eventAdapter = new GroupEventAdapter<>(HomePage.this, 0, 0, events);
        lvAllEvents.setAdapter(eventAdapter);

        oldEventAdapter = new GroupEventAdapter<>(HomePage.this, 0, 0, oldEvents);
        lvOldEvents.setAdapter(oldEventAdapter);
        LocalDate currentDate = LocalDate.now();

        databaseService.getUserEvents(id, new DatabaseService.DatabaseCallback<List<GroupEvent>>() {
            @Override
            public void onCompleted(List<GroupEvent> object) {
                Log.d("TAG", "onCompleted: " + object);
                events.clear();
                oldEvents.clear();





                for (GroupEvent event : object) {

                        // Parse full date+time

                        String stDate = event.getDate().substring(6) + "-" + event.getDate().substring(3, 5) + "-" + event.getDate().substring(0, 2);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate date1 = LocalDate.parse(stDate, formatter);

                        LocalDate date2 = LocalDate.parse(currentDate.toString(), formatter);


                        if (date1.isAfter(date2) || date1.equals(date2)) {

                            events.add(event);
                        }
                        else oldEvents.add(event);
                    }


                eventAdapter.notifyDataSetChanged();
                oldEventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Log.d("Not ", "onCompleted: " + e.getMessage());
            }
        });

        lvAllEvents.setOnItemClickListener((parent, view, position, id) -> {
            selectedEvent = events.get(position);
            Intent intent = new Intent(HomePage.this, EventDetailActivity.class);
            intent.putExtra("event", selectedEvent);
            startActivity(intent);
        });

        lvOldEvents.setOnItemClickListener((parent, view, position, id) -> {
            selectedEvent = oldEvents.get(position);
            Intent intent = new Intent(HomePage.this, EventDetailActivity.class);
            intent.putExtra("event", selectedEvent);
            startActivity(intent);
        });
    }

    private void initViews() {
        lvAllEvents = findViewById(R.id.lvAllEvents);
        lvOldEvents = findViewById(R.id.lvOldEvents);

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
    }

    private void deleteEvent() {
        if (selectedEvent != null) {
            String eventId = selectedEvent.getId();

            databaseService.deleteEventForUser(selectedEvent, id, new DatabaseService.DatabaseCallback<Void>() {
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

    private void removeEventFromList(String eventId) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(eventId)) {
                events.remove(i);
                eventAdapter.notifyDataSetChanged();
                return;
            }
        }
        for (int i = 0; i < oldEvents.size(); i++) {
            if (oldEvents.get(i).getId().equals(eventId)) {
                oldEvents.remove(i);
                oldEventAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

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
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        if (!LoginActivity.isAdmin) {
            menu.removeItem(R.id.menuManagerPage);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuitem) {
        int itemid = menuitem.getItemId();

        if (itemid == R.id.menuAddEvent) {
            startActivity(new Intent(HomePage.this, calender.class));
        }
        if (itemid == R.id.menuLogOut) {
            startActivity(new Intent(HomePage.this, MainPage.class));
        }
        if (itemid == R.id.menuHomePage) {
            startActivity(new Intent(HomePage.this, HomePage.class));
        }
        if (itemid == R.id.menuManagerPage && LoginActivity.isAdmin) {
            startActivity(new Intent(HomePage.this, ManagerPage.class));
        }
        if (itemid == R.id.menuAbout) {
            startActivity(new Intent(HomePage.this, About.class));
        }

        return super.onOptionsItemSelected(menuitem);
    }
}
