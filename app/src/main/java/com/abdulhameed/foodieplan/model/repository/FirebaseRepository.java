package com.abdulhameed.foodieplan.model.repository;

import android.content.Context;
import android.util.Log;

import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.firebase.DeleteMealCallback;
import com.abdulhameed.foodieplan.model.firebase.MealsBackUpCallBack;
import com.abdulhameed.foodieplan.model.local.MealDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRepository {

    private static FirebaseRepository repository;
    private Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private FirebaseUser currentUser;

    private FirebaseRepository(Context context) {
        this.context = context;
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public static synchronized FirebaseRepository getInstance(Context context) {
        if (repository == null) {
            repository = new FirebaseRepository(context);
        }
        return repository;
    }

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }
}