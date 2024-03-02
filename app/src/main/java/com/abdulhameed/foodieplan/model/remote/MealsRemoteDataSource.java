// MealsRemoteDataSourceImp.java
package com.abdulhameed.foodieplan.model.remote;

import android.util.Log;

import com.abdulhameed.foodieplan.model.data.Meal;
import com.abdulhameed.foodieplan.model.data.Category;
import com.abdulhameed.foodieplan.model.data.Country;
import com.abdulhameed.foodieplan.model.data.Ingredient;
import com.abdulhameed.foodieplan.model.data.response.CategoriesResponse;
import com.abdulhameed.foodieplan.model.data.response.CountriesResponse;
import com.abdulhameed.foodieplan.model.data.response.FilterMealResponse;
import com.abdulhameed.foodieplan.model.data.response.IngredientsResponse;
import com.abdulhameed.foodieplan.model.data.FilterMeal;
import com.abdulhameed.foodieplan.model.data.response.MealResponse;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MealsRemoteDataSource {

    public static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private static MealsRemoteDataSource mealsRemoteDataSource;
    private final MealsApi mealsApi;

    private MealsRemoteDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        mealsApi = retrofit.create(MealsApi.class);
    }

    public static synchronized MealsRemoteDataSource getInstance() {
        if (mealsRemoteDataSource == null)
            mealsRemoteDataSource = new MealsRemoteDataSource();
        return mealsRemoteDataSource;
    }

    private static final String TAG = "MealsRemoteDataSource";
    public void getRandomMeal(NetworkCallBack<List<Meal>> networkCallBack) {
        Single<MealResponse> randomMealCall = mealsApi.getRandomMeal();

        Log.d(TAG, "makeRandomMealCall: ");
        randomMealCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    networkCallBack.onSuccess(response.meals);
                    }, error ->
                        networkCallBack.onFailure("Network request failed. " +
                                "Error: " + error.getMessage()));
    }

    public void makeIngredientsCall(NetworkCallBack<List<Ingredient>> networkCallBack) {
        Single<IngredientsResponse> ingredientsCall = mealsApi.getIngredients();

        ingredientsCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    networkCallBack.onSuccess(response.getMeals());
                }, error ->
                        networkCallBack.onFailure("Network request failed. " +
                                "Error: " + error.getMessage()));
    }

    public void makeCategoriesCall(NetworkCallBack<List<Category>> networkCallBack) {
        Single<CategoriesResponse> categoriesCall = mealsApi.getCategories();

        categoriesCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    networkCallBack.onSuccess(response.getCategories());
                }, error ->
                        networkCallBack.onFailure("Network request failed. " +
                                "Error: " + error.getMessage()));
    }

    public void makeCountryCall(NetworkCallBack<List<Country>> networkCallBack) {
        Single<CountriesResponse> countryCall = mealsApi.getArea();

        countryCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    networkCallBack.onSuccess(response.getCountry());
                }, error ->
                        networkCallBack.onFailure("Network request failed. " +
                                "Error: " + error.getMessage()));
    }

    public void getMealById(NetworkCallBack<Meal> networkCallBack, String id) {
        Single<MealResponse> mealsByIdCall = mealsApi.getMealById(id);

        mealsByIdCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    networkCallBack.onSuccess(response.meals.get(0));
                }, error ->
                        networkCallBack.onFailure("Network request failed. " +
                                "Error: " + error.getMessage()));
    }

    public void searchMealByName(NetworkCallBack networkCallBack, String mealName) {
        Single<MealResponse> mealsByCategoryCall = mealsApi.searchByName(mealName);

        mealsByCategoryCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    networkCallBack.onSuccess(response.meals);

                }, error ->
                        networkCallBack.onFailure("Network request failed. " +
                                "Error: " + error.getMessage()));
    }


    public void getMealByIngredient(NetworkCallBack<List<FilterMeal>> networkCallBack, String ingredient) {
        Single<FilterMealResponse> ingtCall = mealsApi.getMealsByIngredient(ingredient);

        ingtCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    networkCallBack.onSuccess(response.getFilter());

                }, error ->
                        networkCallBack.onFailure("Network request failed. " +
                                "Error: " + error.getMessage()));
    }

    public void getMealsByCountry(NetworkCallBack<List<FilterMeal>> networkCallBack, String country) {
        Single<FilterMealResponse> countryCall = mealsApi.getMealsByArea(country);

        countryCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    networkCallBack.onSuccess(response.getFilter());

                }, error ->
                        networkCallBack.onFailure("Network request failed. " +
                                "Error: " + error.getMessage()));
    }

    public void getMealsByCategory(NetworkCallBack<List<FilterMeal>> networkCallBack, String category) {
        Single<FilterMealResponse> mealsByCategoryCall = mealsApi.getMealsByCategory(category);

        mealsByCategoryCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    networkCallBack.onSuccess(response.getFilter());

                }, error ->
                        networkCallBack.onFailure("Network request failed. " +
                                "Error: " + error.getMessage()));
    }
}