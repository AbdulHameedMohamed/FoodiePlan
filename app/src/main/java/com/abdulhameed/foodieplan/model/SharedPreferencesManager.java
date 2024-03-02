package com.abdulhameed.foodieplan.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.abdulhameed.foodieplan.model.data.Meal;
import com.abdulhameed.foodieplan.model.data.User;

public class SharedPreferencesManager {
    private static final String SHARED_PREF_NAME = "MySharedPref";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PROFILE_URL = "imageUrl";
    private static final String KEY_GUEST_MODE = "guest_mode";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_MEAL_NAME = "meal_name";
    private static final String KEY_MEAL_CATEGORY = "meal_category";
    private static final String KEY_MEAL_COUNTRY = "meal_country";
    private static final String KEY_MEAL_ID = "meal_id";
    private static final String KEY_MEAL_THUMB = "meal_thumb";

    private static SharedPreferencesManager instance;
    private final SharedPreferences sharedPreferences;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_USERNAME, user.getUserName());
        editor.putString(KEY_PROFILE_URL, user.getProfileUrl());
        editor.apply();
    }

    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }
    public User getUser() {
        String userId = sharedPreferences.getString(KEY_USER_ID, null);
        String email = sharedPreferences.getString(KEY_EMAIL, null);
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        String imageUrl = sharedPreferences.getString(KEY_PROFILE_URL, null);

        if (email == null)
            return null;

        return new User(userId, email, username, imageUrl);
    }

    public void clearUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PROFILE_URL);
        editor.apply();
    }

    public void saveGuestMode(Boolean guestMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_GUEST_MODE, guestMode);
        editor.apply();
    }

    public Boolean isGuest() {
        return sharedPreferences.getBoolean(KEY_GUEST_MODE, false);
    }

    public void saveLanguage(String language) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LANGUAGE, language);
        editor.apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(KEY_LANGUAGE, "english");
    }

    public void saveDarkMode(Boolean darkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_DARK_MODE, darkMode);
        editor.apply();
    }

    public Boolean getDarkMode() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }
    public void saveMealOfTheDay(Meal meal) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MEAL_ID, meal.getId());
        editor.putString(KEY_MEAL_NAME, meal.getName());
        editor.putString(KEY_MEAL_COUNTRY, meal.getCountry());
        editor.putString(KEY_MEAL_CATEGORY, meal.getCategory());
        editor.putString(KEY_MEAL_THUMB, meal.getThumb());

        editor.apply();
    }

    public Meal getMealOfTheDay() {
        String id = sharedPreferences.getString(KEY_MEAL_ID, null);
        String name = sharedPreferences.getString(KEY_MEAL_NAME, null);
        String country = sharedPreferences.getString(KEY_MEAL_COUNTRY, null);
        String category = sharedPreferences.getString(KEY_MEAL_CATEGORY, null);
        String thumb = sharedPreferences.getString(KEY_MEAL_THUMB, null);
        if(id == null)
            return null;
        return new Meal(id, name, category, country, thumb);
    }

    public void saveMealIdForDay(String day, String mealId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(day, mealId);
        editor.apply();
    }

    public void clearMealForDay(String day) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(day, null);
        editor.apply();
    }

    public String getMealIdForDay(String day) {
        return sharedPreferences.getString(day, null);
    }
}