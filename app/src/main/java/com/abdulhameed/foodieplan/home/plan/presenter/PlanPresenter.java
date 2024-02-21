package com.abdulhameed.foodieplan.home.plan.presenter;


import com.abdulhameed.foodieplan.home.plan.PlanContract;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.PlannedMeal;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.remote.NetworkCallBack;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PlanPresenter implements PlanContract.Presenter, NetworkCallBack<Meal> {

    private final SharedPreferencesManager preferencesManager;
    private final MealRepository repository;
    private final PlanContract.View view;
    private final FavouriteRepository favouriteRepository;


    public PlanPresenter(PlanContract.View view, MealRepository mealRepository, FavouriteRepository favouriteRepository, SharedPreferencesManager preferencesManager) {
        this.repository = mealRepository;
        this.favouriteRepository = favouriteRepository;
        this.preferencesManager = preferencesManager;
        this.view = view;
    }

    @Override
    public void getPlannedMeals() {
        String[] daysOfWeek = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        AtomicInteger mealCounter = new AtomicInteger(0);

        List<PlannedMeal> plannedMeals = new ArrayList<>();

        for (String day : daysOfWeek) {
            String mealId = preferencesManager.getMealIdForDay(day);
            if (mealId != null) {
                repository.getMealsById(new NetworkCallBack<Meal>() {
                    @Override
                    public void onSuccess(Meal meal) {
                        plannedMeals.add(new PlannedMeal(day, meal));

                        int count = mealCounter.incrementAndGet();

                        if (count == daysOfWeek.length) {
                            view.showPlannedMeals(plannedMeals);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        view.showMessage(error);
                    }
                }, mealId);
            } else {
                mealCounter.incrementAndGet();

                if (mealCounter.get() == daysOfWeek.length) {
                    view.showPlannedMeals(plannedMeals);
                }
            }
        }
    }

    @Override
    public void deleteMeal(PlannedMeal meal) {
        preferencesManager.clearMealForDay(meal.getDay());
        view.mealDeleted(meal);
    }

    @Override
    public void addMeal(PlannedMeal meal) {
        preferencesManager.saveMealIdForDay(meal.getDay(), meal.getId());
        view.mealInserted(meal);
    }

    @Override
    public void addFavourite(PlannedMeal plannedMeal) {
        Meal meal = new Meal(plannedMeal);
        repository.insertMeal(meal);
        User user = preferencesManager.getUser();
        if (user.getId() != null) {
            view.mealInserted(plannedMeal);
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
    public void onSuccess(Meal meal) {

    }

    @Override
    public void onFailure(String message) {

    }
}
