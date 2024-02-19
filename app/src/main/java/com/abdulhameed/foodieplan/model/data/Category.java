package com.abdulhameed.foodieplan.model.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Category implements Serializable, Filter {
	@SerializedName("strCategory")
	private String name;

	@SerializedName("strCategoryDescription")
	private String description;

	@SerializedName("idCategory")
	private String id;

	@SerializedName("strCategoryThumb")
	private String thumb;

	public String getName(){
		return name;
	}

	public String getDescription(){
		return description;
	}

	public String getId(){
		return id;
	}

	public String getThumb(){
		return thumb;
	}
}