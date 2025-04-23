package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

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

        buttonsaveeventchanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ensure at least 2 users are selected (event creator + one more)
                if (members.size() < 2) {
                    Toast.makeText(EditExistingEvent.this, "Please select at least one more user to save the event.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String eventName = etEventName.getText().toString().trim();
                String eventDescription = etEventDescription.getText().toString().trim();
                String eventDate = etEventDate.getText().toString().trim();
                String eventTime = etEventTime.getText().toString().trim();

                selectedEvent.setName(eventName);
                selectedEvent.setDate(eventDate);
                selectedEvent.setDetails(eventDescription);
                selectedEvent.setTime(eventTime);
                selectedEvent.setUsers(members);

                databaseService.createNewGroupEvent(selectedEvent, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {
                        Toast.makeText(EditExistingEvent.this, "Event updated successfully", Toast.LENGTH_SHORT).show();

                        databaseService.setEventForUsers(selectedEvent, new DatabaseService.DatabaseCallback<Void>() {
                            @Override
                            public void onCompleted(Void object) {
                                Toast.makeText(EditExistingEvent.this, "EventUser updated successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailed(Exception e) {
                                Toast.makeText(EditExistingEvent.this, "Failed to update event users", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(EditExistingEvent.this, "Failed to update event", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        buttonbacktoEvent.setOnClickListener(v -> {
            Intent intent = new Intent(EditExistingEvent.this, EventDetailActivity.class);
            startActivity(intent);
        });

        adapterMembers = new UserNamAdapter(EditExistingEvent.this, 0, 0, members);
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