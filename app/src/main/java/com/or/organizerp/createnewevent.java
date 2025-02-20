package com.or.organizerp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class createnewevent extends AppCompatActivity {

    private static final String TAG = "AddNewEvent";

    EditText edtEventName, edtDescription, edtEventDate;
    Spinner spType;
    Button btnSubmitEvent, btnBackToCalender;

    private DatabaseService databaseService;
    private ArrayList<User> users = new ArrayList<>();
    private ListView lvMembers, lvSelectedMembers;
    private UserNamAdapter<User> adapter;
    private UserNamAdapter<User> selectedAdapter;
    private ArrayList<User> usersSelected = new ArrayList<>();
    private String uid;
    private String selectedDate;  // To hold the selected date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnewevent);

        databaseService = DatabaseService.getInstance();
        uid = getIntent().getStringExtra("userId"); // Retrieve the user ID from the intent

        initViews();

        // Fetch users from the database
        databaseService.getUsers(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> object) {
                users.clear();
                users.addAll(object);
                adapter.notifyDataSetChanged();
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

        // Submit Event
        btnSubmitEvent.setOnClickListener(v -> {
            addGroupEventToDatabase();
        });
    }

    private void initViews() {
        edtEventName = findViewById(R.id.edtEventName);
        edtDescription = findViewById(R.id.edtDescription);
        edtEventDate = findViewById(R.id.edtEventDate);
        spType = findViewById(R.id.spCreateEvent);
        btnSubmitEvent = findViewById(R.id.btnSubmitEvent);
        btnBackToCalender = findViewById(R.id.btnbacktocalender);
        lvMembers = findViewById(R.id.lvMembers2);
        lvSelectedMembers = findViewById(R.id.lvSelected);

        adapter = new UserNamAdapter<>(this, 0, 0, users);
        lvMembers.setAdapter(adapter);

        selectedAdapter = new UserNamAdapter<>(this, 0, 0, usersSelected);
        lvSelectedMembers.setAdapter(selectedAdapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.ArryEventType, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter2);

        // Retrieve the selected date from the Intent (if available)
        selectedDate = getIntent().getStringExtra("selectedDate");
        if (selectedDate != null && !selectedDate.isEmpty()) {
            // Format the selected date and set it in the EditText
            edtEventDate.setText(selectedDate);
        } else {
            edtEventDate.setText("No date selected"); // Default message
        }
    }

    private void addGroupEventToDatabase() {
        String eventName = edtEventName.getText().toString().trim();
        String eventDescription = edtDescription.getText().toString().trim();
        String eventType = spType.getSelectedItem().toString();
        String eventDate = edtEventDate.getText().toString().trim();

        if (eventName.isEmpty() || eventDescription.isEmpty() || eventType == null || eventDate.isEmpty()) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store event details in SharedPreferences (optional)
        SharedPreferences preferences = getSharedPreferences("events", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("eventName", eventName);
        editor.putString("eventDescription", eventDescription);
        editor.putString("eventType", eventType);
        editor.putString("eventDate", eventDate);
        editor.apply();

        String eventId = databaseService.generateGroupEventId();
        GroupEvent groupEvent = new GroupEvent(eventId, eventName, eventType, eventDate, "", eventDescription, 1, null, null, null, null);

        databaseService.createNewGroupEvent(groupEvent, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(createnewevent.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(createnewevent.this, HomePage.class);
                startActivity(intent);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(createnewevent.this, "Failed to create event", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

