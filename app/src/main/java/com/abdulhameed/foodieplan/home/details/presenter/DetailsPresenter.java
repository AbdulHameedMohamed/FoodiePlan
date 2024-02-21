package com.abdulhameed.foodieplan.home.details.presenter;

import com.abdulhameed.foodieplan.home.details.DetailsContract;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.PlannedMeal;
import com.abdulhameed.foodieplan.model.data.WatchedMeal;
import com.abdulhameed.foodieplan.model.remote.NetworkCallBack;
import com.abdulhameed.foodieplan.model.repository.MealRepository;

import java.util.List;

public class DetailsPresenter implements DetailsContract.Presenter, NetworkCallBack<Meal> {
    private final DetailsContract.View view;
    private final MealRepository repository;
    private final SharedPreferencesManager preferencesManager;

    public DetailsPresenter(DetailsContract.View view, MealRepository repository, SharedPreferencesManager preferencesManager) {
        this.view = view;
        this.repository = repository;
        this.preferencesManager = preferencesManager;
    }

    @Override
    public void getMealById(String id) {
        repository.getMealsById(this, id);
    }

    @Override
    public void addToFavourite(Meal meal) {
        repository.insertMeal(meal);
    }

    @Override
    public void removeFromFavourite(Meal meal) {
        repository.deleteMeal(meal);
    }

    @Override
    public void saveWatchedMeal(WatchedMeal meal) {
        repository.insertWatchedMeal(meal);
    }

    @Override
    public void savePlannedMeal(String day, String plannedId) {
        preferencesManager.saveMealIdForDay(day, plannedId);
    }

    @Override
    public void onSuccess(Meal meal) {
        view.showMeal(meal);
    }

    @Override
    public void onFailure(String message) {
        view.showErrorMsg(message);
    }
}