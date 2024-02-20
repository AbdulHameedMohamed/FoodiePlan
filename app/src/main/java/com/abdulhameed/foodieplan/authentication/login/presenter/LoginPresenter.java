package com.abdulhameed.foodieplan.authentication.login.presenter;

import android.content.Intent;

import com.abdulhameed.foodieplan.authentication.MainActivity;
import com.abdulhameed.foodieplan.authentication.login.LoginContract;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
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
    SharedPreferencesManager preferencesManager;
    public LoginPresenter(LoginContract.View view, AuthenticationRepository authenticationRepository,
                          FavouriteRepository favouriteRepository, MealRepository mealRepository, SharedPreferencesManager preferencesManager) {
        this.view = view;
        this.authenticationRepository = authenticationRepository;
        this.favouriteRepository =  favouriteRepository;
        this.mealRepository = mealRepository;
        this.preferencesManager = preferencesManager;
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
        authenticationRepository.signInAsGuest(new AuthenticationRepository.LoginGuestCallback() {
            @Override
            public void onLoginSuccess(FirebaseUser user) {
                preferencesManager.saveGuestMode(true);
                MainActivity.setGuest(true);
                view.navigateToHomeActivity(user.getUid());
            }

            @Override
            public void onLoginFailed(String errorMessage) {
                view.showAuthenticationFailedError(errorMessage);
            }
        });
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
            preferencesManager.saveGuestMode(false);
            MainActivity.setGuest(false);
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
                    view.showMessage(errorMessage);
                }
            });
        } else {
            view.showMessage("User is null");
        }
    }

    @Override
    public void onLoginFailed(String errorMessage) {
        view.showAuthenticationFailedError(errorMessage);
    }

    @Override
    public void onLoginWithGoogleSuccess(FirebaseUser user) {
        preferencesManager.saveGuestMode(false);
        MainActivity.setGuest(false);
        view.onGoogleSignInSuccess(user);
    }

    @Override
    public void onLoginWithGoogleFailed(String errorMessage) {
        view.showMessage(errorMessage);
    }

    @Override
    public void onSuccess(List<Meal> meals) {
        MainActivity.setGuest(false);
        mealRepository.setMeals(meals);
    }

    @Override
    public void onError(String errorMessage) {
        view.showMessage(errorMessage);
    }
}