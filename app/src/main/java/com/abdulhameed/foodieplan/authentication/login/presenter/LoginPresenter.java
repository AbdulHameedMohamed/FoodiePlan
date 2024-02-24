package com.abdulhameed.foodieplan.authentication.login.presenter;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.abdulhameed.foodieplan.authentication.login.LoginContract;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class LoginPresenter implements LoginContract.Presenter, AuthenticationRepository.LoginCallback, AuthenticationRepository.LoginWithGoogleCallback, FavouriteRepository.Callback<List<Meal>>, AuthenticationRepository.LoginGuestCallback, AuthenticationRepository.UserDataListener {
    private final LoginContract.View view;
    private final AuthenticationRepository authenticationRepository;
    private final FavouriteRepository favouriteRepository;
    private final MealRepository mealRepository;
    SharedPreferencesManager preferencesManager;
    private static final String TAG = "LoginPresenter";

    private LoginPresenter(Builder presenterBuilder) {
        this.view = presenterBuilder.view;
        this.authenticationRepository = presenterBuilder.authenticationRepository;
        this.favouriteRepository = presenterBuilder.favouriteRepository;
        this.mealRepository = presenterBuilder.mealRepository;
        this.preferencesManager = presenterBuilder.sharedPreferencesManager;
    }

    @Override
    public void signInWithEmail(String email, String password) {
        view.showLoading();
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

    public void handleGoogleSignInResult(Intent data) {
        authenticationRepository.handleGoogleSignInResult(data, this);
    }

    @NonNull
    private static User getUser(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();
        String email = firebaseUser.getEmail();
        String username = firebaseUser.getDisplayName();
        String imageUrl = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : "";
        return new User(userId, email, username, imageUrl);
    }

    @Override
    public void onGuestLoginSuccess(FirebaseUser firebaseUser) {
        Log.d(TAG, "onLoginSuccess: ");
        preferencesManager.saveGuestMode(true);
        preferencesManager.saveUserId(firebaseUser.getUid());
        view.navigateToHomeActivity(firebaseUser.getUid());
    }

    @Override
    public void onLoginSuccess(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            favouriteRepository.getAllMealsForUser(firebaseUser.getUid(), this);
            preferencesManager.saveGuestMode(false);
            Log.d(TAG, "onLoginSuccess: " + firebaseUser);
            authenticationRepository.getUserData(firebaseUser.getUid(), this);
        } else {
            view.showMessage("User is Not Found");
        }
    }

    @Override
    public void onLoginFailed(String errorMessage) {
        view.showAuthenticationFailedError(errorMessage);
    }

    @Override
    public void onLoginWithGoogleSuccess(FirebaseUser firebaseUser) {
        preferencesManager.saveGuestMode(false);
        User user = getUser(firebaseUser);
        preferencesManager.saveUser(user);
        Log.d(TAG, "onLoginWithGoogleSuccess: " + preferencesManager.isGuest());
        view.onGoogleSignInSuccess(firebaseUser);
    }

    @Override
    public void onLoginWithGoogleFailed(String errorMessage) {
        view.showMessage(errorMessage);
    }

    @Override
    public void onSuccess(List<Meal> meals) {
        if (meals != null && !meals.isEmpty())
            mealRepository.setMeals(meals);
    }

    @Override
    public void onError(String errorMessage) {
        view.showMessage(errorMessage);
    }

    @Override
    public void onUserDataReceived(User user) {
        Log.d(TAG, "onLoginSuccess: " + user);
        preferencesManager.saveUser(user);
        view.navigateToHomeActivity(user.getId());
    }

    @Override
    public void onUserDataCancelled(String errorMessage) {
        view.showMessage(errorMessage);
    }

    public static class Builder {
        private final LoginContract.View view;
        private AuthenticationRepository authenticationRepository;
        private FavouriteRepository favouriteRepository;
        private MealRepository mealRepository;
        private SharedPreferencesManager sharedPreferencesManager;

        public Builder(LoginContract.View view) {
            this.view = view;
        }

        public Builder setAuthenticationRepository(AuthenticationRepository authenticationRepository) {
            this.authenticationRepository = authenticationRepository;
            return this;
        }

        public Builder setFavouriteRepository(FavouriteRepository favouriteRepository) {
            this.favouriteRepository = favouriteRepository;
            return this;
        }

        public Builder setMealRepository(MealRepository mealRepository) {
            this.mealRepository = mealRepository;
            return this;
        }

        public Builder setSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
            this.sharedPreferencesManager = sharedPreferencesManager;
            return this;
        }

        public LoginPresenter build() {
            return new LoginPresenter(this);
        }
    }
}