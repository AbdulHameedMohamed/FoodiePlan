package com.abdulhameed.foodieplan.home.favourite;

import android.util.Log;

import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;

import java.util.List;

public class FavouritePresenter implements FavouriteContract.Presenter, FavouriteRepository.Callback<List<Meal>> {
    private final FavouriteContract.View view;
    private final MealRepository mealRepository;
    private final FavouriteRepository favouriteRepository;

    public FavouritePresenter(FavouriteContract.View view, MealRepository mealRepository, FavouriteRepository favouriteRepository){
        this.view = view;
        this.mealRepository = mealRepository;
        this.favouriteRepository = favouriteRepository;
    }

    @Override
    public void getFavouriteMeals() {
        view.showData(mealRepository.getAllMealsFromLocal());
        Log.i("TAG", "getData: " + User.getCurrentUserId());
    }

    @Override
    public void removeFavouriteMeal(String userId, Meal meal) {
        if (userId != null && meal.getId() != null) {
            mealRepository.deleteMeal(meal);
            favouriteRepository.deleteMealForUser(userId, meal.getId(), new FavouriteRepository.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    view.showMessage("Removed Successfully");
                }

                @Override
                public void onError(String errorMessage) {
                    view.showError(errorMessage);
                }
            });
        } else {
            view.showError("No User, Login First");
        }
    }

    @Override
    public void onSuccess(List<Meal> meals) {
        if(meals!= null && meals.size() != 0) {
            mealRepository.setMeals(meals);
            getFavouriteMeals();
        } else {
            view.showNoFavourites();
        }
    }

    @Override
    public void onError(String errorMessage) {
        view.showError(errorMessage);
    }
}