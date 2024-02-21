package com.abdulhameed.foodieplan.home.favourite;

import androidx.lifecycle.LiveData;
import com.abdulhameed.foodieplan.model.Meal;
import java.util.List;

public interface FavouriteContract {
    interface View {
        void showData(LiveData<List<Meal>> meals);
        void deleteMeal(Meal mealsItem);
        void showNoFavourites();
        void showMessage(String message);

        void mealDeleted(Meal meal);

        void mealInserted(Meal meal);
    }

    interface Presenter {
        void getFavouriteMeals();
        void removeMeal(Meal meal);

        void savePlannedMeal(String day, String mealId);

        void addMeal(Meal meal);
    }
}
