package com.abdulhameed.foodieplan.model.remote;

import com.abdulhameed.foodieplan.model.data.response.CategoriesResponse;
import com.abdulhameed.foodieplan.model.data.response.CountriesResponse;
import com.abdulhameed.foodieplan.model.data.response.FilterMealResponse;
import com.abdulhameed.foodieplan.model.data.response.IngredientsResponse;
import com.abdulhameed.foodieplan.model.data.response.MealResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealsApi {
    @GET("random.php")
    Single<MealResponse> getRandomMeal();
    @GET("list.php?i=list")
    Single<IngredientsResponse> getIngredients();
    @GET("categories.php")
    Single<CategoriesResponse> getCategories();
    @GET("list.php?a=list")
    Single<CountriesResponse> getArea();

    @GET("lookup.php")
    Single<MealResponse> getMealById(@Query("i") String id);

    @GET("search.php")
    Single<MealResponse> searchByName(@Query("s") String mealName);

    @GET("filter.php")
    Single<FilterMealResponse> getMealsByIngredient(@Query("i") String ingredient);
    @GET("filter.php")
    Single<FilterMealResponse> getMealsByCategory(@Query("c") String category);
    @GET("filter.php")
    Single<FilterMealResponse> getMealsByArea(@Query("a") String area);
}
