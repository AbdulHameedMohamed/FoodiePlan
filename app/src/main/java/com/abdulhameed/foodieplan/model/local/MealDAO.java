package com.abdulhameed.foodieplan.model.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.abdulhameed.foodieplan.model.data.Meal;

import java.util.List;

@Dao
public interface MealDAO {

    @Query("SELECT * FROM meals")
    LiveData<List<Meal>> getAllMeal();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Meal mealsItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllMeals(List<Meal> mealsItems);

    @Delete
    void delete(Meal mealsItem);

    @Query("SELECT * FROM meals WHERE id= :id")
    LiveData<List<Meal>> getMealsById(String id);

    @Query("DELETE FROM meals")
    void deleteAllFav();

    @Query("SELECT COUNT(*) FROM meals")
    LiveData<Integer> getMealCount();

    @Query("delete from meals")
    void deleteAllMeals();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addMeals(List<Meal> meals);
}