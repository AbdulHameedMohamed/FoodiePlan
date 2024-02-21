package com.abdulhameed.foodieplan.home.favourite.presenter;

import com.abdulhameed.foodieplan.home.favourite.FavouriteContract;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;

import java.util.List;

public class FavouritePresenter implements FavouriteContract.Presenter, FavouriteRepository.Callback<List<Meal>> {
    private final FavouriteContract.View view;
    private final MealRepository mealRepository;
    private final FavouriteRepository favouriteRepository;
    SharedPreferencesManager preferencesManager;

    public FavouritePresenter(FavouriteContract.View view,
                              MealRepository mealRepository,
                              FavouriteRepository favouriteRepository,
                              SharedPreferencesManager preferencesManager){
        this.view = view;
        this.mealRepository = mealRepository;
        this.favouriteRepository = favouriteRepository;
        this.preferencesManager = preferencesManager;
    }

    @Override
    public void getFavouriteMeals() {
        view.showData(mealRepository.getAllMealsFromLocal());
    }

    @Override
    public void removeMeal(Meal meal) {
        User user = preferencesManager.getUser();
        if (user.getId() != null) {
            mealRepository.deleteMeal(meal);
            view.mealDeleted(meal);
            favouriteRepository.deleteMealForUser(user.getId(), meal.getId(), new FavouriteRepository.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    view.showMessage("Removed Successfully");
                }

                @Override
                public void onError(String errorMessage) {
                    view.showMessage(errorMessage);
                }
            });
        } else {
            view.showMessage("No User, Login First");
        }
    }

    @Override
    public void addMeal(Meal meal) {
        User user = preferencesManager.getUser();
        if (user.getId() != null) {
            mealRepository.insertMeal(meal);
            view.mealInserted(meal);
            favouriteRepository.saveMealForUser(user.getId(), meal, new FavouriteRepository.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    view.showMessage("Added Successfully");
                }

                @Override
                public void onError(String errorMessage) {
                    view.showMessage(errorMessage);
                }
            });
        } else {
            view.showMessage("No User, Login First");
        }
    }

    @Override
    public void savePlannedMeal(String day, String mealId) {
        preferencesManager.saveMealIdForDay(day, mealId);
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
        view.showMessage(errorMessage);
    }
}