package com.abdulhameed.foodieplan.model.remote;

public interface NetworkCallBack<T> {
    void onSuccess(T result);
    void onFailure(String message);
}