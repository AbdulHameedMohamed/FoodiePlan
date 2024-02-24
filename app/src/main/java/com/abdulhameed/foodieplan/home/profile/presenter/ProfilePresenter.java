package com.abdulhameed.foodieplan.home.profile.presenter;

import android.net.Uri;

import com.abdulhameed.foodieplan.home.profile.ProfileContract;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;

public class ProfilePresenter implements ProfileContract.Presenter, AuthenticationRepository.GetUserCallback, AuthenticationRepository.GetImageCallback {

    private final ProfileContract.View view;
    private final AuthenticationRepository authenticationRepository;
    private final MealRepository mealRepository;
    private final SharedPreferencesManager preferencesManager;

    public ProfilePresenter(ProfileContract.View view, AuthenticationRepository authenticationRepository
            , MealRepository mealRepository, SharedPreferencesManager preferencesManager) {
        this.view = view;
        this.authenticationRepository = authenticationRepository;
        this.mealRepository = mealRepository;
        this.preferencesManager = preferencesManager;
    }
    @Override
    public void logOut() {
        authenticationRepository.logOut();
        preferencesManager.clearUser();
        clearFavourites();
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
    public void getDownloadUserImage() {
        authenticationRepository.downloadUserImage(this);
    }

    @Override
    public void btnSignupClicked() {
        mealRepository.deleteAllMeals();
    }

    @Override
    public void onGetUserSuccess(User user) {
        view.showUserData(user);
    }
    @Override
    public void onGetUserFailed(String errorMessage) {
        view.showErrorMessage(errorMessage);
    }

    @Override
    public void onDownloadImgSuccess(String url) {
        view.displayImage(url);
    }

    @Override
    public void onSaveUserFailed(String errorMessage) {
        view.showErrorMessage(errorMessage);
    }
}