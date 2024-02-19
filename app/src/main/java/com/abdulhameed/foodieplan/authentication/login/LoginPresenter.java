package com.abdulhameed.foodieplan.authentication.login;

import android.content.Intent;

import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.abdulhameed.foodieplan.utils.NetworkManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginPresenter implements LoginContract.Presenter, AuthenticationRepository.LoginCallback, AuthenticationRepository.LoginWithGoogleCallback {
    private final LoginContract.View view;
    private final AuthenticationRepository repository;

    public LoginPresenter(LoginContract.View view, AuthenticationRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void signInWithEmail(String email, String password) {
        repository.login(email, password, this);
    }

    private void storeUserData(String userId, String email, String username, String photoUrl) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        User newUser = new User(userId, email, username, photoUrl);
        usersRef.child(userId).setValue(newUser)
                .addOnSuccessListener(aVoid -> {
                    // User data stored successfully
                })
                .addOnFailureListener(e -> {
                    // Failed to store user data
                });
    }

    public void handleGoogleSignInResult(Intent data) {
        repository.handleGoogleSignInResult(data, this);
    }

    @Override
    public void onLoginSuccess(FirebaseUser user) {
        view.showLoading();
        if (user != null) {
            // Get user information
            String userId = user.getUid();
            String email = user.getEmail();
            String username = user.getDisplayName();
            String imageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";
            repository.saveUserToDatabase(userId, email, username, imageUrl, new AuthenticationRepository.SaveUserCallback() {
                @Override
                public void onSaveUserSuccess() {
                    view.onGoogleSignInSuccess(user);
                }

                @Override
                public void onSaveUserFailed(String errorMessage) {
                    view.onGoogleSignInFailed(errorMessage);
                }
            });
        } else {
            view.onGoogleSignInFailed("User is null");
        }
    }

    @Override
    public void onLoginFailed(String errorMessage) {
        view.showAuthenticationFailedError(errorMessage);
    }

    @Override
    public void onLoginWithGoogleSuccess(FirebaseUser user) {
        view.onGoogleSignInSuccess(user);
    }

    @Override
    public void onLoginWithGoogleFailed(String errorMessage) {
        view.onGoogleSignInFailed(errorMessage);
    }
}