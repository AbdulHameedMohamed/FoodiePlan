package com.abdulhameed.foodieplan.home.details.presenter;

import com.abdulhameed.foodieplan.home.details.DetailsContract;
import com.abdulhameed.foodieplan.model.data.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.WatchedMeal;
import com.abdulhameed.foodieplan.model.remote.NetworkCallBack;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.google.firebase.auth.FirebaseAuth;

public class DetailsPresenter implements DetailsContract.Presenter, NetworkCallBack<Meal> {
    private final DetailsContract.View view;
    private final MealRepository repository;
    private final SharedPreferencesManager preferencesManager;
    private final FavouriteRepository favouriteRepository;
    private final MealRepository mealsRepository;

    public DetailsPresenter(DetailsContract.View view, MealRepository mealsRepository, FavouriteRepository favouriteRepository, SharedPreferencesManager preferencesManager) {
        this.view = view;
        this.repository = mealsRepository;
        this.preferencesManager = preferencesManager;
        this.mealsRepository = mealsRepository;
        this.favouriteRepository = favouriteRepository;
    }

    @Override
    public void getMealById(String id) {
        repository.getMealsById(this, id);
    }

    @Override
    public void addToFavourite(Meal meal) {

        mealsRepository.insertMeal(meal);
        favouriteRepository.saveMealForUser(FirebaseAuth.getInstance().getUid(), meal, new FavouriteRepository.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean isInserted) {
            }

            @Override
            public void onError(String errorMessage) {
                view.showMsg(errorMessage);
            }
        });
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
        view.showMsg(message);
    }
}