package com.abdulhameed.foodieplan.model.data.response;

import com.abdulhameed.foodieplan.model.data.FilterMeal;

import java.util.List;

public class FilterMealResponse{

	private List<FilterMeal> meals;

	public List<FilterMeal> getFilter(){
		return meals;
	}
}