package com.abdulhameed.foodieplan.model.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.abdulhameed.foodieplan.model.data.Meal;
import com.abdulhameed.foodieplan.model.data.WatchedMeal;
import com.abdulhameed.foodieplan.model.remote.WatchedMealDao;

@Database(entities = {Meal.class, WatchedMeal.class}, version = 1)
public abstract class MealDatabase extends RoomDatabase {

    private static MealDatabase instance = null;

    public abstract MealDAO getMealDAO();
    public abstract WatchedMealDao getWatchedMealDAO();

    public static synchronized MealDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext() , MealDatabase.class ,"mealdb")
                    .build();
        }
        return instance;
    }
}