package com.abdulhameed.foodieplan.authentication.login;

import android.content.Intent;

import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LoginPresenter implements LoginContract.Presenter, AuthenticationRepository.LoginCallback, AuthenticationRepository.LoginWithGoogleCallback, FavouriteRepository.Callback<List<Meal>> {
    private final LoginContract.View view;
    private final AuthenticationRepository authenticationRepository;
    private final FavouriteRepository favouriteRepository;
    private final MealRepository mealRepository;
    public LoginPresenter(LoginContract.View view, AuthenticationRepository authenticationRepository,
                          FavouriteRepository favouriteRepository, MealRepository mealRepository) {
        this.view = view;
        this.authenticationRepository = authenticationRepository;
        this.favouriteRepository =  favouriteRepository;
        this.mealRepository = mealRepository;
    }

    @Override
    public void signInWithEmail(String email, String password) {
        authenticationRepository.login(email, password, this);
    }

    @Override
    public void getFavouriteMeals(String userId) {

        favouriteRepository.getAllMealsForUser(userId, this);
    }

    @Override
    public void signInAsGuest() {
        authenticationRepository.signInAsGuest(this);
    }

    private void storeUserData(String userId, String email, String username, String photoUrl) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        User newUser = new User(userId, email, username, photoUrl);
        usersRef.child(userId).setValue(newUser)
                .addOnSuccessListener(aVoid -> {
                    // User data stored successfully
                })
                .addOnFailureListener(e -> {
                    // Failed to store user data
                });
    }

    public void handleGoogleSignInResult(Intent data) {
        authenticationRepository.handleGoogleSignInResult(data, this);
    }

    @Override
    public void onLoginSuccess(FirebaseUser user) {
        view.showLoading();
        if (user != null) {
            // Get user information
            String userId = user.getUid();
            String email = user.getEmail();
            String username = user.getDisplayName();
            String imageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";
            authenticationRepository.saveUserToDatabase(userId, email, username, imageUrl, new AuthenticationRepository.SaveUserCallback() {
                @Override
                public void onSaveUserSuccess() {
                    view.onGoogleSignInSuccess(user);
                }

                @Override
                public void onSaveUserFailed(String errorMessage) {
                    view.onGoogleSignInFailed(errorMessage);
                }
            });
        } else {
            view.onGoogleSignInFailed("User is null");
        }
    }

    @Override
    public void onLoginFailed(String errorMessage) {
        view.showAuthenticationFailedError(errorMessage);
    }

    @Override
    public void onLoginWithGoogleSuccess(FirebaseUser user) {
        view.onGoogleSignInSuccess(user);
    }

    @Override
    public void onLoginWithGoogleFailed(String errorMessage) {
        view.onGoogleSignInFailed(errorMessage);
    }

    @Override
    public void onSuccess(List<Meal> meals) {
        mealRepository.setMeals(meals);
    }

    @Override
    public void onError(String errorMessage) {

    }
}