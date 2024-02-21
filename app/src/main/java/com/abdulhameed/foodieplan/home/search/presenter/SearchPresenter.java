package com.abdulhameed.foodieplan.home.search.presenter;

import android.util.Log;

import com.abdulhameed.foodieplan.home.search.SearchContract;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.remote.NetworkCallBack;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements SearchContract.Presenter, NetworkCallBack<List<Meal>> {

    private final MealRepository mealRepository;
    private final FavouriteRepository favouriteRepository;
    private final SharedPreferencesManager preferencesManager;
    SearchContract.View view;

    public SearchPresenter(SearchContract.View view, MealRepository mealRepository, FavouriteRepository favouriteRepository, SharedPreferencesManager preferencesManager) {
        this.mealRepository = mealRepository;
        this.favouriteRepository = favouriteRepository;
        this.preferencesManager = preferencesManager;
        this.view = view;
    }

    @Override
    public void searchFilterItem(String search) {
        mealRepository.getMealsByName(this, search);
    }

    private static final String TAG = "SearchPresenter";

    @Override
    public void addToFavourite(String userId, Meal meal) {
        mealRepository.insertMeal(meal);
        favouriteRepository.saveMealForUser(userId, meal, new FavouriteRepository.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean isInserted) {
                Log.d(TAG, "addToFavourite: " + isInserted);
            }

            @Override
            public void onError(String errorMessage) {
                view.showErrorMsg(errorMessage);
                Log.d(TAG, "addToFavourite: " + userId);
            }
        });
    }

    @Override
    public void savePlannedMeal(String day, String mealId) {
        preferencesManager.saveMealIdForDay(day, mealId);
    }

    @Override
    public void onSuccess(List<Meal> meals) {
        if(meals == null)
            meals = new ArrayList<>();
        view.showMeals(meals);
    }

    @Override
    public void onFailure(String message) {
        view.showErrorMsg(message);
    }
}
