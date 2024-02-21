package com.abdulhameed.foodieplan.model.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.abdulhameed.foodieplan.model.Meal;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class PlannedMeal {
    private String id;
    private String name;
    private String category;
    private String country;
    private String thumb;
    private String day;
    public PlannedMeal(String day, String id, String name, String category, String country, String thumb) {
        this.day = day;
        this.id = id;
        this.name = name;
        this.category = category;
        this.country = country;
        this.thumb = thumb;
    }

    public PlannedMeal(String day, Meal meal) {
        this(day, meal.getId(), meal.getName(), meal.getCategory(), meal.getCountry(), meal.getThumb());
    }

    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String strCategory) {
        this.category = strCategory;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlannedMeal that = (PlannedMeal) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(category, that.category) && Objects.equals(country, that.country) && Objects.equals(thumb, that.thumb) && Objects.equals(day, that.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, country, thumb, day);
    }
}
