package com.abdulhameed.foodieplan.home.home.presenter;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.abdulhameed.foodieplan.home.home.HomeContract;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.Category;
import com.abdulhameed.foodieplan.model.data.Country;
import com.abdulhameed.foodieplan.model.data.FilterMeal;
import com.abdulhameed.foodieplan.model.data.Ingredient;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.data.WatchedMeal;
import com.abdulhameed.foodieplan.model.remote.NetworkCallBack;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.FilterRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class HomePresenter implements HomeContract.Presenter, NetworkCallBack {
    private HomeContract.View view;
    private final MealRepository mealsRepository;
    private final FavouriteRepository favouriteRepository;
    private final SharedPreferencesManager preferencesManager;
    private static final String TAG = "HomePresenter";

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
    public void getCountries() {
        mealsRepository.getCountries(this);
    }

    @Override
    public void getInterestsMeals() {
        LiveData<List<WatchedMeal>> watchedMeals = mealsRepository.getWatchedMeals();
        view.showWatchedMeals(watchedMeals);
    }

    @Override
    public void savePlannedMeal(String day, String mealId) {
        preferencesManager.saveMealIdForDay(day, mealId);
    }

    @Override
    public void getCountryMeals(String country, FilterRepository repository) {
        repository.getMealsByCountry(new NetworkCallBack<List<FilterMeal>>() {
            @Override
            public void onSuccess(List<FilterMeal> filterMeals) {
                Log.d(TAG, "onSuccess: " + filterMeals);
                if(filterMeals != null) {
                    view.showCountryMeals(filterMeals);
                    Log.d(TAG, "onSuccess: " + filterMeals);
                }
                else {
                    repository.getMealsByCountry(this, "Unknown");
                }
            }

            @Override
            public void onFailure(String message) {
                Log.d(TAG, "onFailure: " + message);
                repository.getMealsByCountry(this, "Unknown");
                view.showError(message);
            }
        }, country);
    }

    @Override
    public void addToFavourite(Meal meal) {
        Log.d(TAG, "addToFavourite: " + preferencesManager.getUser());

        mealsRepository.insertMeal(meal);
        favouriteRepository.saveMealForUser(FirebaseAuth.getInstance().getUid(), meal, new FavouriteRepository.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean isInserted) {
                Log.d(TAG, "addToFavourite: " + isInserted);
                view.showError("Inserted in Cloud Successfully.");
            }

            @Override
            public void onError(String errorMessage) {
                view.showError(errorMessage);
            }
        });
    }

    @Override
    public void removeFromFavourite(Meal meal) {
        if (preferencesManager.getUser() != null && meal.getId() != null) {
            favouriteRepository.deleteMealForUser(preferencesManager.getUser().getId(), meal.getId(), new FavouriteRepository.Callback<Boolean>() {
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