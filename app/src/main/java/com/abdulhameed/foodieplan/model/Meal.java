package com.abdulhameed.foodieplan.model;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.abdulhameed.foodieplan.model.data.WatchedMeal;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "meals")
public class Meal {
    public Meal() {
    }

    public Meal(@NonNull String id, String name, String category, String country, String thumb) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.country = country;
        this.thumb = thumb;
    }

    @PrimaryKey
    @NonNull
    @SerializedName("idMeal")
    private String id;
    @SerializedName("strMeal")
    private String name;
    @Ignore
    private Object strDrinkAlternate;
    @SerializedName("strCategory")
    private String category;
    @SerializedName("strArea")
    private String country;
    @SerializedName("strInstructions")
    private String instructions;
    @SerializedName("strMealThumb")
    private String thumb;
    private String strTags;

    @SerializedName("strYoutube")
    private String videoUrl;

    private String strIngredient1;
    private String strIngredient2;
    private String strIngredient3;
    private String strIngredient4;
    private String strIngredient5;
    private String strIngredient6;
    private String strIngredient7;
    private String strIngredient8;
    private String strIngredient9;
    private String strIngredient10;
    private String strIngredient11;
    private String strIngredient12;
    private String strIngredient13;
    private String strIngredient14;
    private String strIngredient15;
    private String strIngredient16;
    private String strIngredient17;
    private String strIngredient18;
    private String strIngredient19;
    private String strIngredient20;

    private String strMeasure1;
    private String strMeasure2;
    private String strMeasure3;

    private String strMeasure4;
    private String strMeasure5;
    private String strMeasure6;
    private String strMeasure7;
    private String strMeasure8;
    private String strMeasure9;
    private String strMeasure10;
    private String strMeasure11;
    private String strMeasure12;
    private String strMeasure13;
    private String strMeasure14;
    private String strMeasure15;
    private String strMeasure16;
    private String strMeasure17;
    private String strMeasure18;
    private String strMeasure19;
    private String strMeasure20;
    private String strSource;
    @Ignore
    private Object strImageSource;
    @Ignore
    private Object strCreativeCommonsConfirmed;
    @Ignore
    private Object dateModified;

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

    public Object getStrDrinkAlternate() {
        return strDrinkAlternate;
    }

    public void setStrDrinkAlternate(Object strDrinkAlternate) {
        this.strDrinkAlternate = strDrinkAlternate;
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

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String strMealThumb) {
        this.thumb = strMealThumb;
    }

    public String getStrTags() {
        return strTags;
    }

    public void setStrTags(String strTags) {
        this.strTags = strTags;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getStrIngredient1() {
        return strIngredient1;
    }

    public void setStrIngredient1(String strIngredient1) {
        this.strIngredient1 = strIngredient1;
    }

    public String getStrIngredient2() {
        return strIngredient2;
    }

    public void setStrIngredient2(String strIngredient2) {
        this.strIngredient2 = strIngredient2;
    }

    public String getStrIngredient3() {
        return strIngredient3;
    }

    public void setStrIngredient3(String strIngredient3) {
        this.strIngredient3 = strIngredient3;
    }

    public String getStrIngredient4() {
        return strIngredient4;
    }

    public void setStrIngredient4(String strIngredient4) {
        this.strIngredient4 = strIngredient4;
    }

    public String getStrIngredient5() {
        return strIngredient5;
    }

    public void setStrIngredient5(String strIngredient5) {
        this.strIngredient5 = strIngredient5;
    }

    public String getStrIngredient6() {
        return strIngredient6;
    }

    public void setStrIngredient6(String strIngredient6) {
        this.strIngredient6 = strIngredient6;
    }

    public String getStrIngredient7() {
        return strIngredient7;
    }

    public void setStrIngredient7(String strIngredient7) {
        this.strIngredient7 = strIngredient7;
    }

    public String getStrIngredient8() {
        return strIngredient8;
    }

    public void setStrIngredient8(String strIngredient8) {
        this.strIngredient8 = strIngredient8;
    }

    public String getStrIngredient9() {
        return strIngredient9;
    }

    public void setStrIngredient9(String strIngredient9) {
        this.strIngredient9 = strIngredient9;
    }

    public String getStrIngredient10() {
        return strIngredient10;
    }

    public void setStrIngredient10(String strIngredient10) {
        this.strIngredient10 = strIngredient10;
    }

    public String getStrIngredient11() {
        return strIngredient11;
    }

    public void setStrIngredient11(String strIngredient11) {
        this.strIngredient11 = strIngredient11;
    }

    public String getStrIngredient12() {
        return strIngredient12;
    }

    public void setStrIngredient12(String strIngredient12) {
        this.strIngredient12 = strIngredient12;
    }

    public String getStrIngredient13() {
        return strIngredient13;
    }

    public void setStrIngredient13(String strIngredient13) {
        this.strIngredient13 = strIngredient13;
    }

    public String getStrIngredient14() {
        return strIngredient14;
    }

    public void setStrIngredient14(String strIngredient14) {
        this.strIngredient14 = strIngredient14;
    }

    public String getStrIngredient15() {
        return strIngredient15;
    }

    public void setStrIngredient15(String strIngredient15) {
        this.strIngredient15 = strIngredient15;
    }

    public String getStrIngredient16() {
        return strIngredient16;
    }

    public void setStrIngredient16(String strIngredient16) {
        this.strIngredient16 = strIngredient16;
    }

    public String getStrIngredient17() {
        return strIngredient17;
    }

    public void setStrIngredient17(String strIngredient17) {
        this.strIngredient17 = strIngredient17;
    }

    public String getStrIngredient18() {
        return strIngredient18;
    }

    public void setStrIngredient18(String strIngredient18) {
        this.strIngredient18 = strIngredient18;
    }

    public String getStrIngredient19() {
        return strIngredient19;
    }

    public void setStrIngredient19(String strIngredient19) {
        this.strIngredient19 = strIngredient19;
    }

    public String getStrIngredient20() {
        return strIngredient20;
    }

    public void setStrIngredient20(String strIngredient20) {
        this.strIngredient20 = strIngredient20;
    }

    public String getStrMeasure1() {
        return strMeasure1;
    }

    public void setStrMeasure1(String strMeasure1) {
        this.strMeasure1 = strMeasure1;
    }

    public String getStrMeasure2() {
        return strMeasure2;
    }

    public void setStrMeasure2(String strMeasure2) {
        this.strMeasure2 = strMeasure2;
    }

    public String getStrMeasure3() {
        return strMeasure3;
    }

    public void setStrMeasure3(String strMeasure3) {
        this.strMeasure3 = strMeasure3;
    }

    public String getStrMeasure4() {
        return strMeasure4;
    }

    public void setStrMeasure4(String strMeasure4) {
        this.strMeasure4 = strMeasure4;
    }

    public String getStrMeasure5() {
        return strMeasure5;
    }

    public void setStrMeasure5(String strMeasure5) {
        this.strMeasure5 = strMeasure5;
    }

    public String getStrMeasure6() {
        return strMeasure6;
    }

    public void setStrMeasure6(String strMeasure6) {
        this.strMeasure6 = strMeasure6;
    }

    public String getStrMeasure7() {
        return strMeasure7;
    }

    public void setStrMeasure7(String strMeasure7) {
        this.strMeasure7 = strMeasure7;
    }

    public String getStrMeasure8() {
        return strMeasure8;
    }

    public void setStrMeasure8(String strMeasure8) {
        this.strMeasure8 = strMeasure8;
    }

    public String getStrMeasure9() {
        return strMeasure9;
    }

    public void setStrMeasure9(String strMeasure9) {
        this.strMeasure9 = strMeasure9;
    }

    public String getStrMeasure10() {
        return strMeasure10;
    }

    public void setStrMeasure10(String strMeasure10) {
        this.strMeasure10 = strMeasure10;
    }

    public String getStrMeasure11() {
        return strMeasure11;
    }

    public void setStrMeasure11(String strMeasure11) {
        this.strMeasure11 = strMeasure11;
    }

    public String getStrMeasure12() {
        return strMeasure12;
    }

    public void setStrMeasure12(String strMeasure12) {
        this.strMeasure12 = strMeasure12;
    }

    public String getStrMeasure13() {
        return strMeasure13;
    }

    public void setStrMeasure13(String strMeasure13) {
        this.strMeasure13 = strMeasure13;
    }

    public String getStrMeasure14() {
        return strMeasure14;
    }

    public void setStrMeasure14(String strMeasure14) {
        this.strMeasure14 = strMeasure14;
    }

    public String getStrMeasure15() {
        return strMeasure15;
    }

    public void setStrMeasure15(String strMeasure15) {
        this.strMeasure15 = strMeasure15;
    }

    public String getStrMeasure16() {
        return strMeasure16;
    }

    public void setStrMeasure16(String strMeasure16) {
        this.strMeasure16 = strMeasure16;
    }

    public String getStrMeasure17() {
        return strMeasure17;
    }

    public void setStrMeasure17(String strMeasure17) {
        this.strMeasure17 = strMeasure17;
    }

    public String getStrMeasure18() {
        return strMeasure18;
    }

    public void setStrMeasure18(String strMeasure18) {
        this.strMeasure18 = strMeasure18;
    }

    public String getStrMeasure19() {
        return strMeasure19;
    }

    public void setStrMeasure19(String strMeasure19) {
        this.strMeasure19 = strMeasure19;
    }

    public String getStrMeasure20() {
        return strMeasure20;
    }

    public void setStrMeasure20(String strMeasure20) {
        this.strMeasure20 = strMeasure20;
    }

    public List<Pair<String, String>> getIngredients() {
        List<Pair<String, String>> ingredientsList = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            String ingredient = getIngredient(i);
            String measure = getMeasure(i);
            if (ingredient == null || ingredient.isEmpty())
                break;
            ingredientsList.add(new Pair<>(ingredient, measure));
        }
        return ingredientsList;
    }

    private String getIngredient(int index) {
        switch (index) {
            case 1:
                return strIngredient1;
            case 2:
                return strIngredient2;
            case 3:
                return strIngredient3;
            case 4:
                return strIngredient4;
            case 5:
                return strIngredient5;
            case 6:
                return strIngredient6;
            case 7:
                return strIngredient7;
            case 8:
                return strIngredient8;
            case 9:
                return strIngredient9;
            case 10:
                return strIngredient10;
            case 11:
                return strIngredient11;
            case 12:
                return strIngredient12;
            case 13:
                return strIngredient13;
            case 14:
                return strIngredient14;
            case 15:
                return strIngredient15;
            case 16:
                return strIngredient16;
            case 17:
                return strIngredient17;
            case 18:
                return strIngredient18;
            case 19:
                return strIngredient19;
            case 20:
                return strIngredient20;
            default:
                return "";
        }
    }

    private String getMeasure(int index) {
        switch (index) {
            case 1:
                return strMeasure1;
            case 2:
                return strMeasure2;
            case 3:
                return strMeasure3;
            case 4:
                return strMeasure4;
            case 5:
                return strMeasure5;
            case 6:
                return strMeasure6;
            case 7:
                return strMeasure7;
            case 8:
                return strMeasure8;
            case 9:
                return strMeasure9;
            case 10:
                return strMeasure10;
            case 11:
                return strMeasure11;
            case 12:
                return strMeasure12;
            case 13:
                return strMeasure13;
            case 14:
                return strMeasure14;
            case 15:
                return strMeasure15;
            case 16:
                return strMeasure16;
            case 17:
                return strMeasure17;
            case 18:
                return strMeasure18;
            case 19:
                return strMeasure19;
            case 20:
                return strMeasure20;
            default:
                return "";
        }
    }

    public String getStrSource() {
        return strSource;
    }

    public void setStrSource(String strSource) {
        this.strSource = strSource;
    }

    public Object getStrImageSource() {
        return strImageSource;
    }

    public void setStrImageSource(Object strImageSource) {
        this.strImageSource = strImageSource;
    }

    public Object getStrCreativeCommonsConfirmed() {
        return strCreativeCommonsConfirmed;
    }

    public void setStrCreativeCommonsConfirmed(Object strCreativeCommonsConfirmed) {
        this.strCreativeCommonsConfirmed = strCreativeCommonsConfirmed;
    }

    public Object getDateModified() {
        return dateModified;
    }

    public void setDateModified(Object dateModified) {
        this.dateModified = dateModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meal meal = (Meal) o;
        return Objects.equals(id, meal.id) && Objects.equals(name, meal.name) && Objects.equals(strDrinkAlternate, meal.strDrinkAlternate) && Objects.equals(category, meal.category) && Objects.equals(country, meal.country) && Objects.equals(instructions, meal.instructions) && Objects.equals(thumb, meal.thumb) && Objects.equals(strTags, meal.strTags) && Objects.equals(videoUrl, meal.videoUrl) && Objects.equals(strIngredient1, meal.strIngredient1) && Objects.equals(strIngredient2, meal.strIngredient2) && Objects.equals(strIngredient3, meal.strIngredient3) && Objects.equals(strIngredient4, meal.strIngredient4) && Objects.equals(strIngredient5, meal.strIngredient5) && Objects.equals(strIngredient6, meal.strIngredient6) && Objects.equals(strIngredient7, meal.strIngredient7) && Objects.equals(strIngredient8, meal.strIngredient8) && Objects.equals(strIngredient9, meal.strIngredient9) && Objects.equals(strIngredient10, meal.strIngredient10) && Objects.equals(strIngredient11, meal.strIngredient11) && Objects.equals(strIngredient12, meal.strIngredient12) && Objects.equals(strIngredient13, meal.strIngredient13) && Objects.equals(strIngredient14, meal.strIngredient14) && Objects.equals(strIngredient15, meal.strIngredient15) && Objects.equals(strIngredient16, meal.strIngredient16) && Objects.equals(strIngredient17, meal.strIngredient17) && Objects.equals(strIngredient18, meal.strIngredient18) && Objects.equals(strIngredient19, meal.strIngredient19) && Objects.equals(strIngredient20, meal.strIngredient20) && Objects.equals(strMeasure1, meal.strMeasure1) && Objects.equals(strMeasure2, meal.strMeasure2) && Objects.equals(strMeasure3, meal.strMeasure3) && Objects.equals(strMeasure4, meal.strMeasure4) && Objects.equals(strMeasure5, meal.strMeasure5) && Objects.equals(strMeasure6, meal.strMeasure6) && Objects.equals(strMeasure7, meal.strMeasure7) && Objects.equals(strMeasure8, meal.strMeasure8) && Objects.equals(strMeasure9, meal.strMeasure9) && Objects.equals(strMeasure10, meal.strMeasure10) && Objects.equals(strMeasure11, meal.strMeasure11) && Objects.equals(strMeasure12, meal.strMeasure12) && Objects.equals(strMeasure13, meal.strMeasure13) && Objects.equals(strMeasure14, meal.strMeasure14) && Objects.equals(strMeasure15, meal.strMeasure15) && Objects.equals(strMeasure16, meal.strMeasure16) && Objects.equals(strMeasure17, meal.strMeasure17) && Objects.equals(strMeasure18, meal.strMeasure18) && Objects.equals(strMeasure19, meal.strMeasure19) && Objects.equals(strMeasure20, meal.strMeasure20) && Objects.equals(strSource, meal.strSource) && Objects.equals(strImageSource, meal.strImageSource) && Objects.equals(strCreativeCommonsConfirmed, meal.strCreativeCommonsConfirmed) && Objects.equals(dateModified, meal.dateModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, strDrinkAlternate, category, country, instructions, thumb, strTags, videoUrl, strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5, strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10, strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15, strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20, strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5, strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10, strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15, strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20, strSource, strImageSource, strCreativeCommonsConfirmed, dateModified);
    }
}
