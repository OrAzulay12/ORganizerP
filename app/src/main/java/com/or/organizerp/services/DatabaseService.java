package com.or.organizerp.services;

import android.util.Log;

import androidx.annotation.Nullable;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.or.organizerp.model.GroupEvent;
import com.or.organizerp.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/// a service to interact with the Firebase Realtime Database.
/// this class is a singleton, use getInstance() to get an instance of this class
/// @see #getInstance()
/// @see FirebaseDatabase
public class DatabaseService {

    /// tag for logging
    /// @see Log
    private static final String TAG = "DatabaseService";

    public void deleteGroupEvent(String eventId, DatabaseCallback<Void> callback) {
        // Reference to the group event in Firebase using the eventId
        databaseReference.child("groupEvents").child(eventId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Event successfully deleted, invoke the callback
                        callback.onCompleted(null);
                    } else {
                        // If there is an error, pass the exception to the callback
                        callback.onFailed(task.getException());
                    }
                });
    }

    public void getGroupEventById(String eventId, DatabaseCallback<GroupEvent> databaseCallback) {
        // Reference to the specific event node in Firebase using the eventId
        databaseReference.child("groupEvents").child(eventId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If the task is successful, get the event data
                        GroupEvent groupEvent = task.getResult().getValue(GroupEvent.class);

                        if (groupEvent != null) {
                            // Successfully retrieved the event, pass it to the callback
                            databaseCallback.onCompleted(groupEvent);
                        } else {
                            // Event not found, pass null to callback
                            databaseCallback.onFailed(new Exception("Event not found"));
                        }
                    } else {
                        // If there was an error, pass the exception to the callback
                        databaseCallback.onFailed(task.getException());
                    }
                });
    }



    /// callback interface for database operations
    /// @param <T> the type of the object to return
    /// @see DatabaseCallback#onCompleted(Object)
    /// @see DatabaseCallback#onFailed(Exception)
    public interface DatabaseCallback<T> {
        /// called when the operation is completed successfully
        void onCompleted(T object);

        /// called when the operation fails with an exception
        void onFailed(Exception e);
    }

    /// the instance of this class
    /// @see #getInstance()
    private static DatabaseService instance;

    /// the reference to the database
    /// @see DatabaseReference
    /// @see FirebaseDatabase#getReference()
    private final DatabaseReference databaseReference;

    /// use getInstance() to get an instance of this class
    /// @see DatabaseService#getInstance()
    private DatabaseService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /// get an instance of this class
    /// @return an instance of this class
    /// @see DatabaseService
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }


    // private generic methods to write and read data from the database

    /// write data to the database at a specific path
    /// @param path the path to write the data to
    /// @param data the data to write (can be any object, but must be serializable, i.e. must have a default constructor and all fields must have getters and setters)
    /// @param callback the callback to call when the operation is completed
    /// @return void
    /// @see DatabaseCallback
    private void writeData(@NotNull final String path, @NotNull final Object data, final @Nullable DatabaseCallback<Void> callback) {
        databaseReference.child(path).setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback == null) return;
                callback.onCompleted(task.getResult());
            } else {
                if (callback == null) return;
                callback.onFailed(task.getException());
            }
        });
    }

    /// read data from the database at a specific path
    /// @param path the path to read the data from
    /// @return a DatabaseReference object to read the data from
    /// @see DatabaseReference

    private DatabaseReference readData(@NotNull final String path) {
        return databaseReference.child(path);
    }


    /// get data from the database at a specific path
    /// @param path the path to get the data from
    /// @param clazz the class of the object to return
    /// @param callback the callback to call when the operation is completed
    /// @return void
    /// @see DatabaseCallback
    /// @see Class
    private <T> void getData(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<T> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            T data = task.getResult().getValue(clazz);
            callback.onCompleted(data);
        });
    }

    /// generate a new id for a new object in the database
    /// @param path the path to generate the id for
    /// @return a new id for the object
    /// @see String
    /// @see DatabaseReference#push()

    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }

    // end of private methods for reading and writing data

    // public methods to interact with the database

    /// create a new user in the database
    /// @param user the user object to create
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive void
    ///            if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see User
    
    public void createNewUser(@NotNull final User user, @Nullable final DatabaseCallback<Void> callback) {
        writeData("Users/" + user.getId(), user, callback);
    }

    /// create a new groupEvent in the database
    /// @param groupEvent the groupEvent object to create
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive void
    ///             if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see GroupEvent
    public void createNewGroupEvent(@NotNull final GroupEvent groupEvent, @Nullable final DatabaseCallback<Void> callback) {
        writeData("groupEvents/" + groupEvent.getId(), groupEvent, callback);
    }




    /// get a user from the database
    /// @param uid the id of the user to get
    /// @param callback the callback to call when the operation is completed
    ///               the callback will receive the user object
    ///             if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see User
    public void getUser(@NotNull final String uid, @NotNull final DatabaseCallback<User> callback) {
        getData("Users/" + uid, User.class, callback);
    }



    /// get a groupEvent from the database
    /// @param groupEventId the id of the groupEvent to get
    /// @param callback the callback to call when the operation is completed
    ///               the callback will receive the groupEvent object
    ///              if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see GroupEvent
    public void getGroupEvent(@NotNull final String groupEventId, @NotNull final DatabaseCallback<GroupEvent> callback) {
        getData("groupEvents/" + groupEventId, GroupEvent.class, callback);
    }



    /// generate a new id for a new groupEvent in the database
    /// @return a new id for the groupEvent
    /// @see #generateNewId(String)
    /// @see GroupEvent
    public String generateGroupEventId() {
        return generateNewId("groupEvents");
    }

    /// generate a new id for a new cart in the database
    /// @return a new id for the cart
    /// @see #generateNewId(String)
    /// @see Cart
    public String generateCartId() {
        return generateNewId("carts");
    }

    /// get all the groupEvents from the database
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive a list of groupEvent objects
    ///            if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see List
    /// @see GroupEvent
    /// @see #getData(String, Class, DatabaseCallback)
    public void getGroupEvents(@NotNull final DatabaseCallback<List<GroupEvent>> callback) {
        readData("groupEvents").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<GroupEvent> groupEvents = new ArrayList<>();
            task.getResult().getChildren().forEach(dataSnapshot -> {
                GroupEvent groupEvent = dataSnapshot.getValue(GroupEvent.class);
                Log.d(TAG, "Got groupEvent: " + groupEvent);
                groupEvents.add(groupEvent);
            });

            callback.onCompleted(groupEvents);
        });
    }


    /// get all the users from the database
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive a list of groupEvent objects
    ///            if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see List
    /// @see GroupEvent
    /// @see #getData(String, Class, DatabaseCallback)
    public void getUsers(@NotNull final DatabaseCallback<List<User>> callback) {
        readData("Users").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<User> users = new ArrayList<>();
            task.getResult().getChildren().forEach(dataSnapshot -> {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "Got user: " + user);
                users.add(user);
            });

            callback.onCompleted(users);
        });
    }


}
