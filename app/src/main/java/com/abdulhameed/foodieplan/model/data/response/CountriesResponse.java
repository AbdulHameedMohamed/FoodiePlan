package com.abdulhameed.foodieplan.model.data.response;

import com.abdulhameed.foodieplan.model.data.Country;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountriesResponse {

	@SerializedName("meals")
	private List<Country> country;

	public List<Country> getCountry(){
		return country;
	}
}