package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.or.organizerp.adapter.UserNamAdapter;
import com.or.organizerp.model.GroupEvent;
import com.or.organizerp.model.User;
import com.or.organizerp.services.DatabaseService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class EditExistingEvent extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    Button buttonbacktoEvent, buttonsaveeventchanges;
    private Intent takeit;
    private GroupEvent selectedEvent = null;
    private UserNamAdapter<User> adapterMembers;
    private UserNamAdapter<User> AllUsersAdapter;
    private ArrayList<User> members = new ArrayList<>();
    private ArrayList<User> allUsers = new ArrayList<>();
    private ArrayList<User> deletedMembers = new ArrayList<>();
    private ListView lvShowMembersInGroup, lvallMembers;
    EditText etEventName, etEventDate, etEventTime, etEventDescription;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_existing_event);

        databaseService = DatabaseService.getInstance();
        takeit = getIntent();
        selectedEvent = (GroupEvent) takeit.getSerializableExtra("event");

        initViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (selectedEvent != null) {
            loadEventDetails();
            loadEventMembers();
        }
    }

    private void initViews() {
        etEventName = findViewById(R.id.etEventName);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventDate = findViewById(R.id.etEventDate);
        etEventTime = findViewById(R.id.etEventTime);
        lvShowMembersInGroup = findViewById(R.id.lvEditMembers);
        lvallMembers = findViewById(R.id.lvEditAllMembers);
        buttonbacktoEvent = findViewById(R.id.btnbacktoEvent);
        buttonsaveeventchanges = findViewById(R.id.btnSaveEventChanges);

        buttonsaveeventchanges.setOnClickListener(v -> {
            if (members.size() < 2) {
                Toast.makeText(EditExistingEvent.this, "Please select at least one more user to save the event.", Toast.LENGTH_SHORT).show();
                return;
            }

            String eventName = etEventName.getText().toString().trim();
            String eventDescription = etEventDescription.getText().toString().trim();
            String eventDate = etEventDate.getText().toString().trim();
            String eventTime = etEventTime.getText().toString().trim();

            if (!isValidDate(eventDate) || !isValidTime(eventTime)) {
                Toast.makeText(EditExistingEvent.this, "Invalid date or time format.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if combined date and time is in the past
            if (isDateTimeInPast(eventDate, eventTime)) {
                Toast.makeText(EditExistingEvent.this, "You cannot select a past date/time.", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullDateTime = eventDate + " " + eventTime;

            selectedEvent.setName(eventName);
            selectedEvent.setDate(fullDateTime);  // Save both date and time here
            selectedEvent.setDetails(eventDescription);
            selectedEvent.setUsers(members);

            databaseService.createNewGroupEvent(selectedEvent, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(EditExistingEvent.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                    Intent goadmin = new Intent(EditExistingEvent.this, HomePage.class);
                    goadmin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(goadmin);
                    finish();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(EditExistingEvent.this, "Failed to update event", Toast.LENGTH_SHORT).show();
                }
            });
        });

        buttonbacktoEvent.setOnClickListener(v -> {
            Intent intent = new Intent(EditExistingEvent.this, HomePage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        adapterMembers = new UserNamAdapter<>(EditExistingEvent.this, 0, 0, members);
        lvShowMembersInGroup.setAdapter(adapterMembers);
        lvShowMembersInGroup.setOnItemLongClickListener(this);

        AllUsersAdapter = new UserNamAdapter<>(this, 0, 0, allUsers);
        lvallMembers.setAdapter(AllUsersAdapter);
        lvallMembers.setOnItemClickListener(this);
    }

    private void loadEventDetails() {
        if (selectedEvent != null) {
            etEventName.setText(selectedEvent.getName());
            etEventDescription.setText(selectedEvent.getDetails());
            etEventDate.setText(selectedEvent.getDate());
            etEventTime.setText(selectedEvent.getTime());
//
//            // Split full date into date and time fields
//            String fullDateTime = selectedEvent.getDate();
//            if (fullDateTime != null && fullDateTime.contains(" ")) {
//                Log.d("!!!!!!!!!!!!!!!!!!", "#"+fullDateTime+"#");
//                String[] parts = fullDateTime.split(" ");
//                etEventDate.setText(parts[0]);
//                etEventTime.setText(parts.length > 1 ? parts[1] : "");
//            } else {
//
//            }
        } else {
            Toast.makeText(EditExistingEvent.this, "Event details not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadEventMembers() {
        databaseService.getEventMembers(selectedEvent.getId(), new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> users) {
                members.clear();
                members.addAll(users);
                adapterMembers.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("EventDetailActivity", "Failed to load event members", e);
                Toast.makeText(EditExistingEvent.this, "Failed to load members.", Toast.LENGTH_SHORT).show();
            }
        });

        databaseService.getUsers(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> object) {
                allUsers.clear();
                allUsers.addAll(object);
                AllUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private boolean isValidDate(String date) {
        if (!Pattern.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$", date)) return false;
        try {
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidTime(String time) {
        if (!Pattern.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$", time)) return false;
        try {
            new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(time);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // New method to check if the combined date and time is in the past
    private boolean isDateTimeInPast(String date, String time) {
        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date eventDateTime = dateTimeFormat.parse(date + " " + time);

            Date now = new Date();

            return eventDateTime.before(now);
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // treat as valid if parsing fails
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User selectedUser = (User) parent.getItemAtPosition(position);
        boolean found = false;

        for (User member : members) {
            if (member.getId().equals(selectedUser.getId())) {
                found = true;
                break;
            }
        }

        if (!found) {
            members.add(selectedUser);
            adapterMembers.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        User user = (User) parent.getItemAtPosition(position);

        if (!user.getId().equals(selectedEvent.getAdmin().getId())) {
            deletedMembers.add(user);

            databaseService.deleteEventForUser(selectedEvent, user.getId(), new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(EditExistingEvent.this, "User removed from event", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(EditExistingEvent.this, "Failed to remove user", Toast.LENGTH_SHORT).show();
                }
            });

            members.remove(user);
            adapterMembers.notifyDataSetChanged();
        }

        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
