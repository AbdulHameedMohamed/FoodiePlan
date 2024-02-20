package com.abdulhameed.foodieplan.home.plan.presenter;

import android.util.Log;

import com.abdulhameed.foodieplan.home.plan.PlanContract;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.PlannedMeal;
import com.abdulhameed.foodieplan.model.remote.NetworkCallBack;
import com.abdulhameed.foodieplan.model.repository.MealRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PlanPresenter implements PlanContract.Presenter, NetworkCallBack<Meal> {

    private final SharedPreferencesManager preferencesManager;
    private final MealRepository repository;
    private final PlanContract.View view;
    private final List<PlannedMeal> plannedMeals = new ArrayList<>();

    public PlanPresenter(PlanContract.View view, MealRepository repository, SharedPreferencesManager preferencesManager) {
        this.repository = repository;
        this.preferencesManager = preferencesManager;
        this.view = view;
    }

    private static final String TAG = "PlanPresenter";
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
                        view.showErrorMessage(error);
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
    public void onSuccess(Meal meal) {

    }

    @Override
    public void onFailure(String message) {

    }
}
