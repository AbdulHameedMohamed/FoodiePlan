package com.abdulhameed.foodieplan.authentication.signup.presenter;

import android.graphics.Bitmap;
import android.util.Log;

import com.abdulhameed.foodieplan.authentication.signup.SignupContract;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.abdulhameed.foodieplan.utils.SignupValidator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupPresenter implements SignupContract.Presenter, AuthenticationRepository.SignupCallback, AuthenticationRepository.UploadCallback, AuthenticationRepository.LinkGuestCallback {
    private final SignupContract.View view;
    private static final String TAG = "SignupPresenter";
    private final AuthenticationRepository repository;
    private final SharedPreferencesManager preferencesManager;

    public SignupPresenter(SignupContract.View view, AuthenticationRepository repository, SharedPreferencesManager preferencesManager) {
        this.view = view;
        this.preferencesManager = preferencesManager;
        this.repository = repository;
    }

    @Override
    public void signup(User user, String confirmPassword, Bitmap profileImg) {

        if (notValid(user, confirmPassword)) return;

        view.showProgressBar();

        repository.signup(user, profileImg, this);
    }

    private boolean notValid(User user, String confirmPassword) {
        if (!SignupValidator.isValidEmail(user.getEmail())) {
            view.showErrorMessage("Pls Enter Your Email Correct.");
            return true;
        }

        if (!SignupValidator.isValidUsername(user.getUserName())) {
            view.showErrorMessage("Pls Enter Your UserName Correct.");
            return true;
        }

        if (!SignupValidator.isValidPassword(user.getPassword())) {
            view.showErrorMessage("Pls Enter Your Password Correct.");
            return true;
        }

        if (!SignupValidator.isValidPasswordConfirmation(user.getPassword(), confirmPassword)) {
            view.showErrorMessage("Passwords do not match.");
            return true;
        }
        return false;
    }

    @Override
    public void onSignupSuccess(User newUser, Bitmap profileImg) {
        repository.uploadProfileImage(newUser.getId(), profileImg, this);
        if (preferencesManager.isGuest()) {
            repository.mergeFavoritesFromAnonymousUser(newUser, preferencesManager.getUserId());
            preferencesManager.saveGuestMode(false);
        }
        view.hideProgressBar();
        view.navigateToLogin();
    }

    @Override
    public void onSignupFailed(String errorMessage) {
        view.hideProgressBar();
        Log.d(TAG, "onSignupFailed: "+ errorMessage);
        view.showErrorMessage("Sign up Failed \n"+ errorMessage);
    }

    @Override
    public void onUploadSuccess() {
        Log.d(TAG, "onUploadSuccess: ");
    }

    @Override
    public void onUploadFailed(String errorMessage) {
        view.showErrorMessage(errorMessage);
    }

    @Override
    public void onLinkGuestSuccess(FirebaseUser user) {
        Log.d(TAG, "onLinkGuestSuccess: " + user.getDisplayName());
        //repository.mergeFavourites(user);
    }

    @Override
    public void onLinkGuestFailed(String errorMessage) {
        view.showErrorMessage(errorMessage);
    }
}