package com.abdulhameed.foodieplan.model.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.data.Category;
import com.abdulhameed.foodieplan.model.data.Country;
import com.abdulhameed.foodieplan.model.data.Ingredient;
import com.abdulhameed.foodieplan.model.data.WatchedMeal;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.remote.NetworkCallBack;

import java.util.List;

public class MealRepository {
    private static MealRepository mealsRepository;
    private final MealsRemoteDataSource mealsRemoteDataSource;
    private final MealsLocalDataSource mealsLocalDataSource;
    private MealRepository(MealsRemoteDataSource mealsRemoteDataSource, MealsLocalDataSource mealsLocalDataSource) {
        this.mealsRemoteDataSource = mealsRemoteDataSource;
        this.mealsLocalDataSource = mealsLocalDataSource;
    }
    public static MealRepository getInstance(MealsRemoteDataSource mealsRemoteDataSource, MealsLocalDataSource mealsLocalDataSource){
        if (mealsRepository == null){
            mealsRepository = new MealRepository(mealsRemoteDataSource, mealsLocalDataSource);
        }
        return mealsRepository;
    }

    public void getRandomMeal(NetworkCallBack<List<Meal>> networkCallBack) {
        mealsRemoteDataSource.getRandomMeal(networkCallBack);
    }

    public void getIngredients(NetworkCallBack<List<Ingredient>> networkCallBack) {
        mealsRemoteDataSource.makeIngredientsCall(networkCallBack);
    }

    public void getCategories(NetworkCallBack<List<Category>> networkCallBack) {
        mealsRemoteDataSource.makeCategoriesCall(networkCallBack);
    }

    public void getCountry(NetworkCallBack<List<Country>> networkCallBack) {
        mealsRemoteDataSource.makeCountryCall(networkCallBack);
    }

    public void getMealsById(NetworkCallBack<Meal> networkCallBack, String id) {
        mealsRemoteDataSource.getMealById(networkCallBack, id);
    }

    public void getMealsByName(NetworkCallBack<List<Meal>> networkCallBack, String mealName) {
        mealsRemoteDataSource.searchMealByName(networkCallBack, mealName);
    }

    public void insertMeal(Meal mealsItem) {
        mealsLocalDataSource.insertMeal(mealsItem);
    }

    public void deleteMeal(Meal mealsItem) {
        mealsLocalDataSource.deleteMeal(mealsItem);
    }

    public LiveData<List<Meal>> getAllMealsFromLocal() {
        return mealsLocalDataSource.getAllMeals();
    }

    public void deleteAllMeals() {
        mealsLocalDataSource.deleteAllMeals();
    }

    public void setMeals(List<Meal> meals) {
        mealsLocalDataSource.addMeals(meals);
    }

    public void insertWatchedMeal(WatchedMeal meal) {
        mealsLocalDataSource.insertWatchedMeal(meal);
    }

    public LiveData<List<WatchedMeal>> getWatchedMeals() {
        return mealsLocalDataSource.getWatchedMeals();
    }
}