package com.abdulhameed.foodieplan.home.favourite;

import androidx.lifecycle.LiveData;
import com.abdulhameed.foodieplan.model.Meal;
import java.util.List;

public interface FavouriteContract {
    interface View {
        void showData(LiveData<List<Meal>> meals);
        void showError(String message);
        void deleteMeal(Meal mealsItem);
        void afterRemove();

        void showNoFavourites();
    }

    interface Presenter {
        void getFavouriteMeals();
        void removeFavouriteMeal(String userId, Meal meal);

        void getFavouriteMealsFromRemote(String userId);
    }
}
