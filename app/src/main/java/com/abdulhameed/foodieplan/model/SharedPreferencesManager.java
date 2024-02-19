package com.abdulhameed.foodieplan.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.abdulhameed.foodieplan.model.data.PlannedMeal;

public class SharedPreferencesManager {
    private static final String SHARED_PREF_NAME = "MySharedPref";


    private static final String KEY_USER_ID = "userId";

    private static final String KEY_MEAL_NAME = "meal_name";
    private static final String KEY_MEAL_CATEGORY = "meal_category";
    private static final String KEY_MEAL_COUNTRY = "meal_country";
    private static final String KEY_MEAL_ID = "meal_id";
    private static final String KEY_MEAL_THUMB = "meal_thumb";

    private static final String KEY_PLANNED_MEAL_ID = "planned_meal_id";
    private static final String KEY_PLANNED_MEAL_DAY = "planned_meal_day";
    private static final String KEY_PLANNED_MEAL_NAME = "planned_meal_name";
    private static final String KEY_PLANNED_MEAL_CATEGORY = "planned_meal_category";
    private static final String KEY_PLANNED_MEAL_COUNTRY = "planned_meal_country";
    private static final String KEY_PLANNED_MEAL_THUMB = "planned_meal_thumb";

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

    // Save user ID
    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }


    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void clearUserId() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.apply();
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
        return new Meal(id, name, country, category, thumb);
    }

    public void saveMealIdForDay(String day, String mealId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(day, mealId);
        editor.apply();
    }

    public String getMealIdForDay(String day) {
        return sharedPreferences.getString(day, null);
    }
}