package com.or.organizerp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class createnewevent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spCreateEvent;
    Button btnSubmitEvent;
    private String selectedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnewevent);

        // Initialize views
        spCreateEvent = findViewById(R.id.spCreateEvent);

        spCreateEvent.setOnItemSelectedListener(this);




        // Populate Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.ArryEventType, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCreateEvent.setAdapter(adapter);

        // Button Click Listener

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         selectedEvent = spCreateEvent.getSelectedItem().toString();
        Toast.makeText(this, "Event Selected: " + selectedEvent, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
