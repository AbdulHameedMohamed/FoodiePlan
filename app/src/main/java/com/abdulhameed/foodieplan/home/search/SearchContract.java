package com.abdulhameed.foodieplan.home.search;

import com.abdulhameed.foodieplan.model.Meal;

import java.util.List;

public interface SearchContract {
    interface View {
        void showMeals(List<Meal> mealsItems);
        void showEmptyDataMessage();
        void showErrorMsg(String error);
    }

    interface Presenter {
        void searchFilterItem(String searchText);

        void addToFavourite(String userId, Meal meal);

        void savePlannedMeal(String day, String mealId);
    }
}