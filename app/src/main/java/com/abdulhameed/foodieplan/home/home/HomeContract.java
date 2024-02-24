package com.abdulhameed.foodieplan.home.home;

import androidx.lifecycle.LiveData;

import com.abdulhameed.foodieplan.model.data.Category;
import com.abdulhameed.foodieplan.model.data.Country;
import com.abdulhameed.foodieplan.model.data.FilterMeal;
import com.abdulhameed.foodieplan.model.data.Ingredient;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.data.WatchedMeal;
import com.abdulhameed.foodieplan.model.repository.FilterRepository;

import java.util.List;

public interface HomeContract {
    interface View {
        void showMealOfTheDay(Meal meal);
        void showIngredients(List<Ingredient> ingredientItems);
        void showCategory(List<Category> categories);

        void showCountry(List<Country> countryItems);
        void showEmptyDataMessage();
        public void deleteMeal(Meal mealsItem);
        void showError(String message);
        void showWatchedMeals(LiveData<List<WatchedMeal>> watchedMealsLD);

        void showCountryMeals(List<FilterMeal> filterMeals);
    }

    interface Presenter {
        void getMealOfTheDay();

        void detachView();

        void attachView(HomeContract.View homeFragment);

        void addToFavourite(Meal meal);

        void removeFromFavourite(Meal meal);

        void getIngredients();

        void getCategories();

        void getCountries();

        void getInterestsMeals();

        void savePlannedMeal(String day, String mealId);

        void getCountryMeals(String country, FilterRepository instance);
    }
}