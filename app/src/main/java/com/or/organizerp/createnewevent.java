package com.or.organizerp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.or.organizerp.adapter.UserNamAdapter;
import com.or.organizerp.model.GroupEvent;
import com.or.organizerp.model.User;
import com.or.organizerp.services.DatabaseService;
import com.or.organizerp.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class createnewevent extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    EditText edtEventName, edtDescription, edtEventDate, edtTime;
    Spinner spType;
    Button btnSubmitEvent, btnBackToCalender;

    private DatabaseService databaseService;
    private ArrayList<User> users = new ArrayList<>();
    private ListView lvMembers, lvSelectedMembers;
    private UserNamAdapter<User> adapter;
    private UserNamAdapter<User> selectedAdapter;
    private ArrayList<User> usersSelected = new ArrayList<>();

    private String selectedDateInMillis;  // To hold the selected date in milliseconds
    private User user=null;
    private View btnAddMedicnNoti;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnewevent);


        initViews();


        databaseService = DatabaseService.getInstance();
       user= SharedPreferencesUtil.getUser(createnewevent.this); // Retrieve the user ID from the intent

        // Initialize the adapter before trying to use it
        adapter = new UserNamAdapter<>(this, 0, 0, users);
        lvMembers.setAdapter(adapter);

        lvMembers.setOnItemClickListener(this);

        usersSelected.add(user);

        selectedAdapter = new UserNamAdapter<>(this, 0, 0, usersSelected);
        lvSelectedMembers.setAdapter(selectedAdapter);
        lvSelectedMembers.setOnItemLongClickListener(this);


        // Fetch users from the database
        databaseService.getUsers(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> object) {
                users.clear();
                users.addAll(object);
                // Now that the data is loaded, we can notify the adapter
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });

        // Back to Calendar
        btnBackToCalender.setOnClickListener(v -> {
            Intent intent = new Intent(createnewevent.this, calender.class);
            startActivity(intent);
        });

        // Retrieve the selected date (in milliseconds) from the Intent
        selectedDateInMillis = getIntent().getStringExtra("selectedDate");

        // Format and display the selected date in the EditText
        if (!selectedDateInMillis.isEmpty()) {
         //  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
          //  String formattedDate = sdf.format(new Date(selectedDateInMillis));
            edtEventDate.setText(selectedDateInMillis);
        } else {
            edtEventDate.setText("No date selected");
        }

        // Set up the spinner for event type
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.ArryEventType, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter2);

        // Submit Event
        btnSubmitEvent.setOnClickListener(v -> addGroupEventToDatabase());
    }

    private void initViews() {
        // Initialize views
        edtEventName = findViewById(R.id.edtEventName);
        edtDescription = findViewById(R.id.edtDescription);
        edtEventDate = findViewById(R.id.edtEventDate);
        edtTime = findViewById(R.id.edtEventTime);
        spType = findViewById(R.id.spCreateEvent);
        btnSubmitEvent = findViewById(R.id.btnSubmitEvent);
        btnBackToCalender = findViewById(R.id.btnbacktocalender);
        lvMembers = findViewById(R.id.lvMembers2);
        lvSelectedMembers = findViewById(R.id.lvSelected);
    }

    private void addGroupEventToDatabase() {
        String eventName = edtEventName.getText().toString().trim();
        String eventDescription = edtDescription.getText().toString().trim();
        String eventType = spType.getSelectedItem().toString();  // Get the selected event type from Spinner
        String eventDate = edtEventDate.getText().toString().trim();
        String eventTime = edtTime.getText().toString().trim();


        if (eventName.isEmpty() || eventDescription.isEmpty() || eventType == null || eventDate.isEmpty()) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if at least 2 users are selected (current user + 1 more)
        if (usersSelected.size() < 2) {
            Toast.makeText(this, "Please select at least one more user to create the event.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store event details in SharedPreferences (optional)
        SharedPreferences preferences = getSharedPreferences("events", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("eventName", eventName);
        editor.putString("eventDescription", eventDescription);
        editor.putString("eventType", eventType);  // Save the event type here
        editor.putString("eventDate", eventDate);  // Save the formatted date as string
        editor.putString("eventTime", eventTime);
        editor.apply();

        // Create GroupEvent object
        String eventId = databaseService.generateGroupEventId();
        GroupEvent groupEvent = new GroupEvent(eventId, eventName, eventType, eventDate, eventTime, eventDescription, 1, user, usersSelected, null, null);

        // Add the event to the database
        databaseService.createNewGroupEvent(groupEvent, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(createnewevent.this, "Event created successfully", Toast.LENGTH_SHORT).show();

                databaseService.setEventForUsers(groupEvent, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {
                        Toast.makeText(createnewevent.this, "EventUser  created successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });

                Intent intent = new Intent(createnewevent.this, HomePage.class);
                startActivity(intent);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(createnewevent.this, "Failed to create event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User selectedUser = (User) parent.getItemAtPosition(position);

        // Ensure the selected user is not the current user
        if (!selectedUser.getId().equals(user.getId())) {
            boolean found = false;

            // Check if the user is already in the list
            for (int i = 0; i < usersSelected.size(); i++) {
                if (usersSelected.get(i).getId().equals(selectedUser.getId())) {
                    found = true;
                }
            }

            // Only allow adding if the user is not already in the list
            if (!found) {
                usersSelected.add(selectedUser);
                selectedAdapter.notifyDataSetChanged();
            }
        }


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
       User user = (User) parent.getItemAtPosition(position);

       usersSelected.remove(user);

        selectedAdapter.notifyDataSetChanged();


        return false;
    }

           }
