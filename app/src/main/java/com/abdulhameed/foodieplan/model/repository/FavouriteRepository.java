package com.abdulhameed.foodieplan.model.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.abdulhameed.foodieplan.model.Meal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouriteRepository {
    private static FavouriteRepository instance;
    private final DatabaseReference usersRef;

    private FavouriteRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
    }

    public static synchronized FavouriteRepository getInstance() {
        if (instance == null) {
            instance = new FavouriteRepository();
        }
        return instance;
    }

    public void saveMealForUser(String userId, Meal meal, final Callback<Boolean> callback) {
        String mealId = meal.getId();
        usersRef.child(userId).child("meals").child(mealId).setValue(meal)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void deleteMealForUser(String userId, String mealId, final Callback<Boolean> callback) {
        usersRef.child(userId).child("meals").child(mealId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    private static final String TAG = "FavouriteRepository";
    public void getAllMealsForUser(String userId, final Callback<List<Meal>> callback) {
        usersRef.child(userId).child("meals").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Meal> meals = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Meal meal = snapshot.getValue(Meal.class);
                    if (meal != null) {
                        Log.d(TAG, "onDataChange: "+meal);
                        meals.add(meal);
                    }
                }
                Log.d(TAG, "onDataChange: " + meals);
                callback.onSuccess(meals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}