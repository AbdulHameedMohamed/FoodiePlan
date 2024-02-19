package com.abdulhameed.foodieplan.model.firebase;

public interface DeleteMealCallback {
    void onSuccess();
    void onFailure(String error);
}
