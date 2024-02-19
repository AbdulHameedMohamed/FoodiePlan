package com.abdulhameed.foodieplan.model.data.response;

import java.util.List;

import com.abdulhameed.foodieplan.model.data.Category;
import com.google.gson.annotations.SerializedName;

public class CategoriesResponse {

	@SerializedName("categories")
	private List<Category> categories;

	public List<Category> getCategories(){
		return categories;
	}
}