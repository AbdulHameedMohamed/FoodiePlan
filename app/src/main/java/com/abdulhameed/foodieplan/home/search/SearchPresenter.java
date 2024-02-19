package com.abdulhameed.foodieplan.home.search;

import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.remote.NetworkCallBack;
import com.abdulhameed.foodieplan.model.repository.MealRepository;

import java.util.List;

public class SearchPresenter implements SearchContract.Presenter, NetworkCallBack<List<Meal>> {

    private final MealRepository repository;
    SearchContract.View view;

    public SearchPresenter(SearchContract.View view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void searchFilterItem(String search) {
        repository.getMealsByName(this, search);
    }

    @Override
    public void onSuccess(List<Meal> meals) {
        if(meals != null && meals.size() > 0)
            view.showMeals(meals);
    }

    @Override
    public void onFailure(String message) {
        view.showErrorMsg(message);
    }
}
