package com.abdulhameed.foodieplan.home.details;

import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.data.WatchedMeal;

public interface DetailsContract {
    interface Presenter {
        void getMealById(String id);
        void addToFavourite(Meal meal);
        void removeFromFavourite(Meal mealsItem);
        void saveWatchedMeal(WatchedMeal meal);

        void savePlannedMeal(String day, String plannedId);
    }

    interface View {
        void showMeal(Meal meal);
        void showMsg(String error);
    }
}