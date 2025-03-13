package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.List;

public class EditExistingEvent extends AppCompatActivity {


     Button buttonbacktoEvent;
    private Intent takeit;
    private GroupEvent selectedEvent=null;
     ListView lvShowMembersInGroup;

    EditText etEventName, etEventType, etEventDate, etEventTime, etEventDescription;

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_existing_event);

        buttonbacktoEvent = findViewById(R.id.btnbacktoEvent);

        buttonbacktoEvent.setOnClickListener(v -> {
            Intent intent = new Intent(EditExistingEvent.this, EventDetailActivity.class);
            startActivity(intent);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        takeit = getIntent();
        selectedEvent = (GroupEvent) takeit.getSerializableExtra("event");
        initViews();



    }

    private void initViews() {

        etEventName = findViewById(R.id.edtEventName);
        etEventDescription = findViewById(R.id.edtDescription);
        etEventDate = findViewById(R.id.edtEventDate);
        etEventType = findViewById(R.id.edtEventType);
        etEventTime = findViewById(R.id.edtEventTime);
        lvShowMembersInGroup = findViewById(R.id.lvShowMembersInGroup);
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

}