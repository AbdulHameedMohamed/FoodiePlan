package com.abdulhameed.foodieplan.model.firebase;

import com.abdulhameed.foodieplan.model.Meal;

import java.util.List;

public interface MealsBackUpCallBack {
    void onSuccess(List<Meal> mealsItems);
    void onFailure(String error);
}