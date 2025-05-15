package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.FillEventHistory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.or.organizerp.adapter.GroupEventAdapter;
import com.or.organizerp.adapter.UserAdapter;
import com.or.organizerp.model.GroupEvent;
import com.or.organizerp.model.User;
import com.or.organizerp.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerPage extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView lvAllUsers;
    private ListView lvAllEvents;
    private GroupEventAdapter groupEventAdapter;
    private UserAdapter userAdapter;

    private ArrayList<GroupEvent> groupEvents;
    private ArrayList<User> users;
    Spinner spType;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_page);  // Make sure the layout is correct

        if (!LoginActivity.isAdmin) {
            // If not admin, exit the activity and go to MainPage
            startActivity(new Intent(ManagerPage.this, MainPage.class));
            finish();
            return;
        }

        // Initialize ListViews
        lvAllUsers = findViewById(R.id.lvAllUsers);
        lvAllEvents = findViewById(R.id.lvAllEvents);
        spType = findViewById(R.id.spinnerEventType);

        // Initialize data containers
        groupEvents = new ArrayList<>();
        users = new ArrayList<>();

        databaseService = DatabaseService.getInstance();





        groupEventAdapter = new GroupEventAdapter(ManagerPage.this, 0, 0, groupEvents);
        lvAllEvents.setAdapter(groupEventAdapter);
        lvAllEvents.setOnItemClickListener(this);
        // Fetch group events from the database asynchronously
        databaseService.getGroupEvents(new DatabaseService.DatabaseCallback<List<GroupEvent>>() {
            @Override
            public void onCompleted(List<GroupEvent> object) {
                if (object != null) {
                    groupEvents.clear(); // Clear the list to avoid duplication
                    groupEvents.addAll(object);
                    lvAllEvents.setAdapter(groupEventAdapter);
                    groupEventAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailed(Exception e) {
                // Handle the error (e.g., show a toast)
            }
        });

        // Set up the spinner for event type
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.ArryEventTypeManager, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter2);

        // Set up the spinner item selection listener to filter events
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, android.view.View selectedItemView, int position, long id) {
                filterEvents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Optionally handle when no item is selected



            }
        });

        // Set up the SearchView for filtering users
        SearchView searchViewUser = findViewById(R.id.searchViewUser);
        searchViewUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Optional: You can handle search submission here if needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter users based on the query text
                filterUsers(newText);
                return true;
            }
        });


        //lvAllUsers.setOnItemClickListener(this);
        // Fetch users from the database asynchronously
        databaseService.getUsers(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> object) {
                if (object != null) {
                    users.clear(); // Clear the list to avoid duplication
                    users.addAll(object);
                    userAdapter = new UserAdapter(ManagerPage.this, 0, 0, users);
                    lvAllUsers.setAdapter(userAdapter);

                }
            }

            @Override
            public void onFailed(Exception e) {
                // Handle the error (e.g., show a toast)
            }
        });


    }

    // Method to filter events based on the selected type
    private void filterEvents() {
        String selectedType = spType.getSelectedItem().toString();

        // Filter events based on the selected type
        List<GroupEvent> filteredEvents = groupEvents.stream()
                .filter(event -> selectedType.equals("All") || event.getType().equals(selectedType))
                .collect(Collectors.toList());

        // Update the ListView with the filtered events
        groupEventAdapter = new GroupEventAdapter(this, 0, 0, new ArrayList<>(filteredEvents));
        lvAllEvents.setAdapter(groupEventAdapter);
    }

    // Method to filter users based on the search query
    private void filterUsers(String query) {
        if (query.isEmpty()) {
            // If the search query is empty, display all users
            userAdapter = new UserAdapter(this, 0, 0, users);
            lvAllUsers.setAdapter(userAdapter);
        } else {
            // Filter the users list based on the query
            List<User> filteredUsers = users.stream()
                    .filter(user -> user.getFname().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());

            // Update the ListView with the filtered users
            userAdapter = new UserAdapter(this, 0, 0, new ArrayList<>(filteredUsers));
            lvAllUsers.setAdapter(userAdapter);
        }
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

        Intent intent = null;
        if (itemid == R.id.menuAddEvent) {
            intent = new Intent(ManagerPage.this, calender.class);
        }
        if (itemid == R.id.menuLogOut) {
            intent = new Intent(ManagerPage.this, MainPage.class);
        }
        if (itemid == R.id.menuManagerPage) {
            if (LoginActivity.isAdmin) {
                intent = new Intent(ManagerPage.this, ManagerPage.class);
            }
        }
        if (itemid == R.id.menuHomePage) {
            intent = new Intent(ManagerPage.this, HomePage.class);
        }
        if (itemid == R.id.menuAbout) {
            intent = new Intent(ManagerPage.this, About.class);
        }
        if (itemid == R.id.menuManagerPage) {

            if(LoginActivity.isAdmin){
                Intent goadmin = new Intent(ManagerPage.this, ManagerPage.class);
                startActivity(goadmin);

            }

        }
        if (intent != null) {
            startActivity(intent);
        }

        return super.onOptionsItemSelected(menuitem);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       GroupEvent selectedEvent = (GroupEvent) parent.getItemAtPosition(position);
        Intent intent = new Intent(ManagerPage.this, EventDetailActivity.class);
        intent.putExtra("event", selectedEvent);
        startActivity(intent); // Use ActivityResultLauncher
    }
}
