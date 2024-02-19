package com.abdulhameed.foodieplan.model.data;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "watched_meals")
public class WatchedMeal {
    @PrimaryKey
    @NonNull
    @SerializedName("idMeal")
    private String id;
    @SerializedName("strMeal")
    private String name;
    @SerializedName("strCategory")
    private String category;
    @SerializedName("strArea")
    private String country;
    @SerializedName("strMealThumb")
    private String thumb;
    public WatchedMeal(@NonNull String id, String name, String category, String country, String thumb) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.country = country;
        this.thumb = thumb;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
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
}
