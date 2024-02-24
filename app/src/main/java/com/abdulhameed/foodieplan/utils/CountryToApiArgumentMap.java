package com.abdulhameed.foodieplan.utils;

import java.util.HashMap;
import java.util.Map;

public class CountryToApiArgumentMap {
    private static final Map<String, String> countryToApiArgumentMap;

    static {
        countryToApiArgumentMap = new HashMap<>();
        countryToApiArgumentMap.put("United States", "American");
        countryToApiArgumentMap.put("United Kingdom", "British");
        countryToApiArgumentMap.put("Canada", "Canadian");
        countryToApiArgumentMap.put("China", "Chinese");
        countryToApiArgumentMap.put("Netherlands", "Dutch");
        countryToApiArgumentMap.put("Egypt", "Egyptian");
        countryToApiArgumentMap.put("France", "French");
        countryToApiArgumentMap.put("Greece", "Greek");
        countryToApiArgumentMap.put("India", "Indian");
        countryToApiArgumentMap.put("Ireland", "Irish");
        countryToApiArgumentMap.put("Italy", "Italian");
        countryToApiArgumentMap.put("Jamaica", "Jamaican");
        countryToApiArgumentMap.put("Japan", "Japanese");
        countryToApiArgumentMap.put("Kenya", "Kenyan");
        countryToApiArgumentMap.put("Malaysia", "Malaysian");
        countryToApiArgumentMap.put("Mexico", "Mexican");
        countryToApiArgumentMap.put("Morocco", "Moroccan");
        countryToApiArgumentMap.put("Poland", "Polish");
        countryToApiArgumentMap.put("Portugal", "Portuguese");
        countryToApiArgumentMap.put("Russia", "Russian");
        countryToApiArgumentMap.put("Spain", "Spanish");
        countryToApiArgumentMap.put("Thailand", "Thai");
        countryToApiArgumentMap.put("Tunisia", "Tunisian");
        countryToApiArgumentMap.put("Turkey", "Turkish");
        countryToApiArgumentMap.put("Vietnam", "Vietnamese");
    }

    public static String getApiArgumentForCountry(String country) {
        return countryToApiArgumentMap.get(country);
    }
}