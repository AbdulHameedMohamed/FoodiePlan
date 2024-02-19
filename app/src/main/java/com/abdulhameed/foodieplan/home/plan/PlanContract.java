package com.abdulhameed.foodieplan.home.plan;

import com.abdulhameed.foodieplan.model.data.PlannedMeal;

import java.util.List;

public interface PlanContract {
    interface View {
        void showErrorMessage(String error);

        void showPlannedMeals(List<PlannedMeal> plannedMeals);
    }
    
    interface Presenter {
        void getPlannedMeals();
    }
}
