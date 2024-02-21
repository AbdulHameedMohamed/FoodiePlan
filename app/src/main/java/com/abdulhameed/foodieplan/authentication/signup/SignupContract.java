package com.abdulhameed.foodieplan.authentication.signup;

import android.graphics.Bitmap;

import com.abdulhameed.foodieplan.model.data.User;

public interface SignupContract {
    interface View {
        void showProgressBar();
        void hideProgressBar();
        void showErrorMessage(String message);
        void navigateToLogin();
    }

    interface Presenter {
        void signup(User user, String confirmPassword, Bitmap profileImg);
    }
}