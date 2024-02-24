package com.abdulhameed.foodieplan.home.profile;

import android.net.Uri;

import com.abdulhameed.foodieplan.model.data.User;

public interface ProfileContract {

    interface Presenter {
        void logOut();

        void onSaveEditClicked(User user, Uri imageUri);

        void getUser(String id);

        void clearFavourites();

        void getDownloadUserImage();

        void btnSignupClicked();
    }

    interface View {
        void showUserData(User user);

        void showErrorMessage(String errorMessage);

        void displayImage(String url);
    }
}