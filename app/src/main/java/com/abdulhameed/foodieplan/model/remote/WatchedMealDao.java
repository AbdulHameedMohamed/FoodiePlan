package com.abdulhameed.foodieplan.model.remote;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.abdulhameed.foodieplan.model.data.WatchedMeal;

import java.util.List;

@Dao
public interface WatchedMealDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(WatchedMeal watchedMeal);

    @Delete
    void delete(WatchedMeal watchedMeal);

    @Query("SELECT * FROM watched_meals")
    LiveData<List<WatchedMeal>> getAllWatchedMeals();
}