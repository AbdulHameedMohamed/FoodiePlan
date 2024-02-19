package com.abdulhameed.foodieplan.authentication.signup;

import android.graphics.Bitmap;

public interface SignupContract {
    interface View {
        void showProgressBar();
        void hideProgressBar();
        void showErrorMessage(String message);
        void navigateToLogin();
    }

    interface Presenter {
        void signup(String email, String userName, String password, String confirmPassword, Bitmap profileImg);
    }
}