package com.abdulhameed.foodieplan.home.home;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.Category;
import com.abdulhameed.foodieplan.model.data.Country;
import com.abdulhameed.foodieplan.model.data.Ingredient;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.data.WatchedMeal;
import com.abdulhameed.foodieplan.model.firebase.DeleteMealCallback;
import com.abdulhameed.foodieplan.model.remote.NetworkCallBack;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.FirebaseRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;

import java.util.List;

public class HomePresenter implements HomeContract.Presenter, NetworkCallBack {
    private HomeContract.View view;
    private final MealRepository mealsRepository;
    private final FavouriteRepository favouriteRepository;
    private final SharedPreferencesManager preferencesManager;

    public HomePresenter(MealRepository mealsRepository, FavouriteRepository repository, SharedPreferencesManager preferencesManager) {
        this.mealsRepository = mealsRepository;
        this.favouriteRepository = repository;
        this.preferencesManager = preferencesManager;
    }

    @Override
    public void getMealOfTheDay() {
        Meal mealOfTheDay = preferencesManager.getMealOfTheDay();
        if(mealOfTheDay == null) {
            mealsRepository.getRandomMeal(this);
        } else {
            view.showMealOfTheDay(mealOfTheDay);
        }
    }

    public void attachView(HomeContract.View view) {
        this.view = view;
    }

    public void detachView() {
        view = null;
    }

    @Override
    public void getIngredients() {
        mealsRepository.getIngredients(this);
    }

    @Override
    public void getCategories() {
        mealsRepository.getCategories(this);
    }

    @Override
    public void getCountry() {
        mealsRepository.getCountry(this);
    }

    @Override
    public void getWatchedMeals() {
        LiveData<List<WatchedMeal>> watchedMeals = mealsRepository.getWatchedMeals();
        view.showWatchedMeals(watchedMeals);
    }

    private static final String TAG = "HomePresenter";

    @Override
    public void addToFavourite(String userId, Meal meal) {
        Log.d(TAG, "addToFavourite: " + userId);
        favouriteRepository.saveMealForUser(userId, meal, new FavouriteRepository.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean isInserted) {
                if (isInserted)
                    mealsRepository.insertMeal(meal);
                Log.d(TAG, "addToFavourite: " + isInserted);
            }

            @Override
            public void onError(String errorMessage) {
                view.showError(errorMessage);
                Log.d(TAG, "addToFavourite: " + userId);
            }
        });
    }

    @Override
    public void removeFromFavourite(String userId, Meal meal) {
        if (userId != null && meal.getId() != null) {
            favouriteRepository.deleteMealForUser(userId, meal.getId(), new FavouriteRepository.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    mealsRepository.deleteMeal(meal);
                }

                @Override
                public void onError(String errorMessage) {
                    view.showError(errorMessage);
                }
            });
        } else {
            view.showError("NO User, Login First");
        }
    }

    @Override
    public void onSuccess(Object result) {
        if (result instanceof List<?>) {
            List<?> resultList = (List<?>) result;

            if (!resultList.isEmpty()) {
                Object item = resultList.get(0);
                if (item instanceof Meal) {
                    List<Meal> meals = (List<Meal>) result;
                    preferencesManager.saveMealOfTheDay(meals.get(0));
                    view.showMealOfTheDay(meals.get(0));
                } else
                if (item instanceof Ingredient) {
                    view.showIngredients((List<Ingredient>) result);
                } else if (item instanceof Category) {
                    view.showCategory((List<Category>) result);
                } else if (item instanceof Country) {
                    view.showCountry((List<Country>) result);
                } else {
                    view.showEmptyDataMessage();
                }
            } else {
                view.showError("Unexpected result type: " + result.getClass().getSimpleName());
            }
        }
    }

    @Override
    public void onFailure(String message) {
        view.showError(message);
    }
}