package com.abdulhameed.foodieplan.model.local;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.data.WatchedMeal;
import com.abdulhameed.foodieplan.model.remote.WatchedMealDao;

import java.util.List;

public class MealsLocalDataSource {

    private static MealsLocalDataSource mealLocalDataSource = null;
    private final MealDAO favouriteDao;
    private final WatchedMealDao watchedMealDao;
    private MealsLocalDataSource(Context context){
        MealDatabase db = MealDatabase.getInstance(context.getApplicationContext());
        favouriteDao = db.getMealDAO();
        watchedMealDao = db.getWatchedMealDAO();
    }

    public static MealsLocalDataSource getInstance(Context context){
        if(mealLocalDataSource == null)
            mealLocalDataSource = new MealsLocalDataSource(context);
        return mealLocalDataSource;
    }

    public void insertMeal(Meal mealsItem) {
        new Thread(() -> favouriteDao.insert(mealsItem)).start();
    }

    public void deleteMeal(Meal mealsItem) {
        new Thread(() -> favouriteDao.delete(mealsItem)).start();
    }

    public LiveData<List<Meal>> getAllMeals() {
        return favouriteDao.getAllMeal();
    }

    public LiveData<List<Meal>> getMeal(String id) {
        return favouriteDao.getMealsById(id);
    }
    public LiveData<Integer> getMealCount() {
        return favouriteDao.getMealCount();
    }

    public void deleteAllMeals() {
        new Thread (()-> favouriteDao.deleteAllMeals()).start();
    }

    public void addMeals(List<Meal> meals) {
        new Thread (() -> favouriteDao.addMeals(meals)).start();
    }

    public void insertWatchedMeal(WatchedMeal meal) {
        new Thread(() -> watchedMealDao.insert(meal)).start();
    }

    public LiveData<List<WatchedMeal>> getWatchedMeals() {
        return watchedMealDao.getAllWatchedMeals();
    }
}
