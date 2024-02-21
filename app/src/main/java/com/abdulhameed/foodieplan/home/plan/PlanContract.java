package com.abdulhameed.foodieplan.home.plan;

import com.abdulhameed.foodieplan.model.data.PlannedMeal;

import java.util.List;

public interface PlanContract {
    interface View {
        void showMessage(String message);
        void showPlannedMeals(List<PlannedMeal> plannedMeals);

        void mealDeleted(PlannedMeal meal);

        void mealInserted(PlannedMeal meal);
    }
    
    interface Presenter {
        void getPlannedMeals();
        void deleteMeal(PlannedMeal meal);

        void addMeal(PlannedMeal meal);

        void addFavourite(PlannedMeal meal);

        void savePlannedMeal(String day, String mealId);
    }
}
