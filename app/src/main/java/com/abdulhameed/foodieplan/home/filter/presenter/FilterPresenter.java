package com.abdulhameed.foodieplan.home.filter.presenter;

import com.abdulhameed.foodieplan.home.filter.FilterContract;
import com.abdulhameed.foodieplan.model.data.FilterMeal;
import com.abdulhameed.foodieplan.model.remote.NetworkCallBack;
import com.abdulhameed.foodieplan.model.repository.FilterRepository;

import java.util.List;

public class FilterPresenter implements FilterContract.FilterPresenter, NetworkCallBack {
    private final FilterContract.FilterView filterView;
    private final FilterRepository repository;

    public FilterPresenter(FilterContract.FilterView mealsView, FilterRepository repository){
        this.filterView = mealsView;
        this.repository = repository;
    }

    @Override
    public void getMealsByIngredient(String ingredient) {
        repository.getMealsByIngredient(this, ingredient);
    }

    @Override
    public void getMealsByCountry(String country) {
        repository.getMealsByCountry(this, country);
    }

    @Override
    public void getMealsByCategory(String category) {
        repository.getMealsByCategory(this, category);
    }

    @Override
    public void onSuccess(Object result) {
        if (result instanceof List<?>) {
            List<?> resultList = (List<?>) result;

            if (!resultList.isEmpty()) {
                Object item = resultList.get(0);

                if (item instanceof FilterMeal) {
                    // Display meals data
                    filterView.showMeals((List<FilterMeal>) result);
                }
            } else {
                filterView.showEmptyDataMessage();
            }
        }
    }

    @Override
    public void onFailure(String message) {
        filterView.showErrorMsg(message);
    }
}
