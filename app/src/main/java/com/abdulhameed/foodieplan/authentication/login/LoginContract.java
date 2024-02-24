package com.abdulhameed.foodieplan.authentication.login;

import android.content.Intent;

import com.google.firebase.auth.FirebaseUser;

public interface LoginContract {
    interface View {
        void showAuthenticationFailedError(String errorMessage);
        void navigateToHomeActivity(String uid);

        void onGoogleSignInSuccess(FirebaseUser user);

        void showMessage(String errorMessage);

        void showLoading();

        void showDialog(String email, String password);
    }

    interface Presenter {
        void signInWithEmail(String email, String password);

        void getFavouriteMeals(String userId);

        void signInAsGuest();

        void clearGuest();

        void handleGoogleSignInResult(Intent data);
    }
}