package com.abdulhameed.foodieplan.home.profile;

import androidx.lifecycle.LiveData;

import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;

public interface ProfileContract {

    interface Presenter {
        void logOut(AuthenticationRepository repository);
        void clearFavourites();
        void btnSignupClicked();
        void getFavMealsCount();
        void getPlannedMealsCount();
        void getUser();
    }

    interface View {
        void showUserData(User user);

        void showErrorMessage(String errorMessage);

        void displayImage(String url);

        void displayMealsCount(LiveData<Integer> mealsCount);

        void showPlannedMealsCount(int countOfDays);
    }
}