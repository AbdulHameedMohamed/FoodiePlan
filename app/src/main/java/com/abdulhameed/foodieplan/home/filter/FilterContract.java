package com.abdulhameed.foodieplan.home.filter;

import com.abdulhameed.foodieplan.model.data.FilterMeal;

import java.util.List;

public interface FilterContract {
    interface FilterPresenter {
        void getMealsByIngredient(String ingredient);
        void getMealsByCountry(String country);
        void getMealsByCategory(String category);
    }

    interface FilterView {
        void showMeals(List<FilterMeal> filter);
        void showEmptyDataMessage();
        void showErrorMsg(String error);
    }

}
