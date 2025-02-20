package com.or.organizerp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.or.organizerp.model.User;

public class SharedPreferencesUtil {

    private static final String PREF_NAME = "com.or.organizerp.PREFERENCE_FILE_KEY";

    // Save a Long value to shared preferences
    public static void saveLong(Context context, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    // Get a Long value from shared preferences
    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultValue);
    }

    // Save a String value to shared preferences
    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Get a String value from shared preferences
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    // Save an Integer value to shared preferences
    public static void saveInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    // Get an Integer value from shared preferences
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    // Clear all data from shared preferences
    public static void clear(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // Remove a specific key from shared preferences
    public static void remove(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    // Check if a key exists in shared preferences
    public static boolean contains(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }

    // Save a User object to shared preferences
    public static void saveUser(Context context, User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uid", user.getId());
        editor.putString("email", user.getEmail());
        editor.putString("password", user.getPassword());
        editor.putString("fName", user.getFname());
        editor.putString("lName", user.getLname());
        editor.putString("phone", user.getPhone());
        editor.apply();
    }

    // Get the User object from shared preferences
    public static User getUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (!isUserLoggedIn(context)) {
            return null;
        }
        String uid = sharedPreferences.getString("uid", "");
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");
        String fName = sharedPreferences.getString("fName", "");
        String lName = sharedPreferences.getString("lName", "");
        String phone = sharedPreferences.getString("phone", "");
        return new User(uid, fName, lName, phone, email, password);
    }

    // Sign out the user by removing user data from shared preferences
    public static void signOutUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("uid");
        editor.remove("email");
        editor.remove("password");
        editor.remove("fName");
        editor.remove("lName");
        editor.remove("phone");
        editor.apply();
    }

    // Check if the user is logged in
    public static boolean isUserLoggedIn(Context context) {
        return contains(context, "uid");
    }
}
