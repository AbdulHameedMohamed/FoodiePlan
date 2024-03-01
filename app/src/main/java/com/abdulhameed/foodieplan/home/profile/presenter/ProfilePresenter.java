package com.abdulhameed.foodieplan.home.profile.presenter;

import android.net.Uri;

import com.abdulhameed.foodieplan.home.profile.ProfileContract;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.abdulhameed.foodieplan.utils.MyCalender;

public class ProfilePresenter implements ProfileContract.Presenter, AuthenticationRepository.GetImageCallback {

    private final ProfileContract.View view;
    private final MealRepository mealRepository;
    private final SharedPreferencesManager preferencesManager;

    public ProfilePresenter(ProfileContract.View view, MealRepository mealRepository, SharedPreferencesManager preferencesManager) {
        this.view = view;
        this.mealRepository = mealRepository;
        this.preferencesManager = preferencesManager;
    }

    @Override
    public void logOut(AuthenticationRepository repository) {
        repository.logOut();
        preferencesManager.clearUser();
        clearFavourites();
    }

    @Override
    public void clearFavourites() {
        mealRepository.deleteAllMeals();
    }

    @Override
    public void btnSignupClicked() {
        mealRepository.deleteAllMeals();
    }

    @Override
    public void getFavMealsCount() {
        view.displayMealsCount(mealRepository.getMealsCount());
    }

    @Override
    public void getPlannedMealsCount() {
        int countOfDays = 0;
        String[] daysOfWeek = MyCalender.getDaysOfWeek();
        for (String day : daysOfWeek)
            if (preferencesManager.getMealIdForDay(day) != null)
                countOfDays++;
        view.showPlannedMealsCount(countOfDays);
    }

    @Override
    public void getUser() {
        User user = preferencesManager.getUser();
        if (user != null)
            view.showUserData(user);
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