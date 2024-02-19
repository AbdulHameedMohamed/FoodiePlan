package com.abdulhameed.foodieplan.model.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FilterMeal implements Serializable {

	@SerializedName("idMeal")
	private String idMeal;
	@SerializedName("strMeal")
	private String name;
	@SerializedName("strMealThumb")
	private String thumb;

	public String getThumb(){
		return thumb;
	}

	public String getIdMeal(){
		return idMeal;
	}

	public String getName(){
		return name;
	}

	public String getImageUrl() {
		return thumb;
	}
}