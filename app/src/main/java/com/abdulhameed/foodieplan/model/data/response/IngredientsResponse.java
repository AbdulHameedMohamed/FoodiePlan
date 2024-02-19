package com.abdulhameed.foodieplan.model.data.response;

import com.abdulhameed.foodieplan.model.data.Ingredient;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IngredientsResponse {

	@SerializedName("meals")
	private List<Ingredient> ingredientItems;

	public List<Ingredient> getMeals(){
		return ingredientItems;
	}
}