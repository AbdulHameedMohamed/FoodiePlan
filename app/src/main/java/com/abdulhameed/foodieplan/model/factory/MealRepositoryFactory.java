package com.abdulhameed.foodieplan.model.factory;

import android.content.Context;

import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.MealRepository;

public class MealRepositoryFactory {
    private static MealRepositoryFactory instance;
    private final MealsRemoteDataSource remoteDataSource;
    private final MealsLocalDataSource localDataSource;

    private MealRepositoryFactory(Context context) {
        remoteDataSource = MealsRemoteDataSource.getInstance();
        localDataSource = MealsLocalDataSource.getInstance(context);
    }

    public static MealRepositoryFactory getInstance(Context context) {
        if (instance == null) {
            instance = new MealRepositoryFactory(context);
        }
        return instance;
    }

    public MealRepository createMealRepository() {
        return MealRepository.getInstance(remoteDataSource, localDataSource);
    }
}