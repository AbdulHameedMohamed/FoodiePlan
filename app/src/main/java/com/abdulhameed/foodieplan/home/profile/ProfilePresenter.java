package com.abdulhameed.foodieplan.home.profile;

import android.net.Uri;

import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;

public class ProfilePresenter implements ProfileContract.Presenter, AuthenticationRepository.GetUserCallback {

    private final ProfileContract.View view;
    private final AuthenticationRepository authenticationRepository;
    private final MealRepository mealRepository;

    public ProfilePresenter(ProfileContract.View view, AuthenticationRepository authenticationRepository, MealRepository mealRepository) {
        this.view = view;
        this.authenticationRepository = authenticationRepository;
        this.mealRepository = mealRepository;
    }
    @Override
    public void logOut() {
        authenticationRepository.logOut();
    }

    @Override
    public void onSaveEditClicked(User user, Uri imageUri) {

    }

    @Override
    public void getUser(String id) {
        authenticationRepository.getUserById(id, this);
    }

    @Override
    public void clearFavourites() {
        mealRepository.deleteAllMeals();
    }

    @Override
    public void onGetUserSuccess(User user) {
        view.showUserData(user);
    }

    @Override
    public void onGetUserFailed(String errorMessage) {

    }
}