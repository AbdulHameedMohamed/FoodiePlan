package com.abdulhameed.foodieplan.model.repository;

import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.remote.NetworkCallBack;

public class FilterRepository {

    private static FilterRepository filterRepo;

    private final MealsRemoteDataSource mealsRemoteDataSource;

    private FilterRepository(MealsRemoteDataSource mealsRemoteDataSource) {
        this.mealsRemoteDataSource = mealsRemoteDataSource;
    }
    public static FilterRepository getInstance(MealsRemoteDataSource mealsRemoteDataSource){
        if (filterRepo == null){
            filterRepo = new FilterRepository(mealsRemoteDataSource);
        }
        return filterRepo;
    }

    public void getMealsByIngredient(NetworkCallBack networkCallBack, String ingredient) {
        mealsRemoteDataSource.getMealByIngredient(networkCallBack, ingredient);
    }

    public void getMealsByCountry(NetworkCallBack networkCallBack, String country) {
        mealsRemoteDataSource.getMealsByCountry(networkCallBack, country);
    }

    public void getMealsByCategory(NetworkCallBack networkCallBack, String category) {
        mealsRemoteDataSource.getMealsByCategory(networkCallBack, category);
    }

    public void getMealById(NetworkCallBack networkCallBack, String id) {
        mealsRemoteDataSource.getMealById(networkCallBack, id);
    }
}
