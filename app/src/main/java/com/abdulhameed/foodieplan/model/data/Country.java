package com.abdulhameed.foodieplan.model.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Country implements Serializable, Filter {

	@SerializedName("strArea")
	private String name;
	@Override
	public String getName(){
		return name;
	}

	@Override
	public String getThumb() {
		return "https://www.themealdb.com/images/icons/flags/big/64/" + getUrlForCountry() + ".png";
	}

	private String getUrlForCountry() {
		switch (name) {
			case "American":
				return "us";
			case "British":
				return "gb";
			case "Canadian":
				return "ca";
			case "Chinese":
				return "cn";
			case "Croatian":
				return "hr";
			case "Dutch":
				return "nl";
			case "Egyptian":
				return "eg";
			case "Filipino":
				return "ph";
			case "French":
				return "fr";
			case "Greek":
				return "gr";
			case "Indian":
				return "in";
			case "Irish":
				return "ie";
			case "Italian":
				return "it";
			case "Jamaican":
				return "jm";
			case "Japanese":
				return "jp";
			case "Kenyan":
				return "ke";
			case "Malaysian":
				return "my";
			case "Mexican":
				return "mx";
			case "Moroccan":
				return "ma";
			case "Polish":
				return "pl";
			case "Portuguese":
				return "pt";
			case "Russian":
				return "ru";
			case "Spanish":
				return "es";
			case "Thai":
				return "th";
			case "Tunisian":
				return "tn";
			case "Turkish":
				return "tr";
			case "Vietnamese":
				return "vn";
			default:
				return "";
		}
	}
}