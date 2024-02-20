package com.abdulhameed.foodieplan.authentication.login;

import com.google.firebase.auth.FirebaseUser;

public interface LoginContract {
    interface View {
        void showAuthenticationFailedError(String errorMessage);
        void navigateToHomeActivity(String uid);

        void onGoogleSignInSuccess(FirebaseUser user);

        void showMessage(String errorMessage);

        void showLoading();
    }

    interface Presenter {
        void signInWithEmail(String email, String password);

        void getFavouriteMeals(String userId);

        void signInAsGuest();
    }
}