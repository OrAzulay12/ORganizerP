package com.or.organizerp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

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

    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnewevent);

        initViews();

        databaseService = DatabaseService.getInstance();
        user = SharedPreferencesUtil.getUser(createnewevent.this);

        adapter = new UserNamAdapter<>(this, 0, 0, users);
        lvMembers.setAdapter(adapter);
        lvMembers.setOnItemClickListener(this);

        usersSelected.add(user);
        selectedAdapter = new UserNamAdapter<>(this, 0, 0, usersSelected);
        lvSelectedMembers.setAdapter(selectedAdapter);
        lvSelectedMembers.setOnItemLongClickListener(this);

        databaseService.getUsers(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> object) {
                users.clear();
                users.addAll(object);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });

        btnBackToCalender.setOnClickListener(v -> {
            Intent intent = new Intent(createnewevent.this, calender.class);
            startActivity(intent);
        });

        // Receive and display date from calendar activity
        String selectedDate = getIntent().getStringExtra("selectedDate");
        if (selectedDate != null && !selectedDate.isEmpty()) {
            edtEventDate.setText(selectedDate);
        } else {
            edtEventDate.setText("");
        }

        // Setup spinner for event type
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.ArryEventType, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter2);

        // Show date picker when clicking on date EditText
        edtEventDate.setOnClickListener(v -> showDatePicker());

        btnSubmitEvent.setOnClickListener(v -> addGroupEventToDatabase());
    }

    private void initViews() {
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

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                    String mon = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
                    edtEventDate.setText(day + "/" + mon + "/" + year);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Prevent selecting past dates
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void addGroupEventToDatabase() {
        String eventName = edtEventName.getText().toString().trim();
        String eventDescription = edtDescription.getText().toString().trim();
        String eventType = spType.getSelectedItem().toString();
        String eventDate = edtEventDate.getText().toString().trim();
        String eventTime = edtTime.getText().toString().trim();

        if (eventName.isEmpty() || eventDescription.isEmpty() || eventType == null || eventDate.isEmpty()) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate date format and that date is not in the past
        if (!isValidDate(eventDate)) {
            Toast.makeText(this, "Please enter a valid date in DD/MM/YYYY format.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isDateInPast(eventDate)) {
            Toast.makeText(this, "The date cannot be in the past.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate time format HH:mm
        if (!isValidTime(eventTime)) {
            Toast.makeText(this, "Please enter a valid time in HH:mm format (00:00 to 23:59).", Toast.LENGTH_SHORT).show();
            return;
        }

        // *** NEW CHECK: prevent past date+time ***
        if (isDateTimeInPast(eventDate, eventTime)) {
            Toast.makeText(this, "You cannot select a past date/time.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usersSelected.size() < 2) {
            Toast.makeText(this, "Please select at least one more user to create the event.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences preferences = getSharedPreferences("events", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("eventName", eventName);
        editor.putString("eventDescription", eventDescription);
        editor.putString("eventType", eventType);
        editor.putString("eventDate", eventDate);
        editor.putString("eventTime", eventTime);
        editor.apply();

        String eventId = databaseService.generateGroupEventId();
        GroupEvent groupEvent = new GroupEvent(eventId, eventName, eventType, eventDate, eventTime, eventDescription, 1, user, usersSelected, null, null);

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

    private boolean isValidDate(String date) {
        // Regex to check format DD/MM/YYYY and valid day/month ranges
        String datePattern = "^([0][1-9]|[12][0-9]|3[01])/([0][1-9]|1[0-2])/([0-9]{4})$";
        if (!Pattern.matches(datePattern, date)) {
            return false;
        }
        // Further validation (e.g. no Feb 30) can be done by parsing:
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isDateInPast(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date chosenDate = sdf.parse(date);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            return chosenDate.before(today.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return true; // treat parse error as invalid/past date
        }
    }

    private boolean isValidTime(String time) {
        // Regex HH:mm 00:00 to 23:59
        String timePattern = "^([01]?\\d|2[0-3]):[0-5]\\d$";
        return Pattern.matches(timePattern, time);
    }

    // *** NEW METHOD: combined date+time check ***
    private boolean isDateTimeInPast(String date, String time) {
        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date eventDateTime = dateTimeFormat.parse(date + " " + time);

            Date now = new Date();

            return eventDateTime.before(now);
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // treat parse failure as valid so as not to block
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User selectedUser = (User) parent.getItemAtPosition(position);

        if (!selectedUser.getId().equals(user.getId())) {
            boolean found = false;
            for (User u : usersSelected) {
                if (u.getId().equals(selectedUser.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                usersSelected.add(selectedUser);
                selectedAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        User userToRemove = (User) parent.getItemAtPosition(position);
        usersSelected.remove(userToRemove);
        selectedAdapter.notifyDataSetChanged();
        return true;
    }
}
