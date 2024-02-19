package com.abdulhameed.foodieplan.model.repository;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.abdulhameed.foodieplan.authentication.login.LoginFragment;
import com.abdulhameed.foodieplan.model.data.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class AuthenticationRepository {
    private final FirebaseAuth mAuth;
    private final FirebaseDatabase mDatabase;
    private final StorageReference mStorageRef;
    private static final String TAG = "AuthenticationRepositor";

    public AuthenticationRepository(FirebaseAuth mAuth, FirebaseDatabase mDatabase, StorageReference mStorageRef) {
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
        this.mStorageRef = mStorageRef;
    }

    public void getUserById(String userId, GetUserCallback callback) {
        DatabaseReference usersRef = mDatabase.getReference("users").child(userId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if user exists
                if (dataSnapshot.exists()) {
                    // User found, parse user data
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        callback.onGetUserSuccess(user);
                    } else {
                        callback.onGetUserFailed("Failed to parse user data.");
                    }
                } else {
                    callback.onGetUserFailed("User not found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onGetUserFailed("Database error: " + databaseError.getMessage());
            }
        });
    }

    public void signup(String email, String password, String username, Bitmap profileImg, SignupCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            uploadProfileImage(user.getUid(), profileImg, new UploadCallback() {
                                @Override
                                public void onUploadSuccess(String imageUrl) {
                                    saveUserData(user.getUid(), email, username, imageUrl, callback);
                                }

                                @Override
                                public void onUploadFailed(String errorMessage) {
                                    callback.onSignupFailed(errorMessage);
                                }
                            });
                        } else {
                            callback.onSignupFailed("Failed to create user.");
                        }
                    } else {
                        callback.onSignupFailed("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    private void uploadProfileImage(String userId, Bitmap profileImg, UploadCallback callback) {
        if (profileImg == null) {
            callback.onUploadSuccess(null);
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profileImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference profileImgRef = mStorageRef.child("profile_images/" + userId + ".jpg");

        profileImgRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    profileImgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        callback.onUploadSuccess(uri.toString());
                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful upload
                    callback.onUploadFailed(exception.getMessage());
                });
    }

    private void saveUserData(String userId, String email, String username, String imageUrl, SignupCallback callback) {
        DatabaseReference usersRef = mDatabase.getReference("users");
        User newUser = new User(userId, email, username, imageUrl);
        usersRef.child(userId).setValue(newUser)
                .addOnSuccessListener(aVoid -> {
                    callback.onSignupSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onSignupFailed("Failed to save user data: " + e.getMessage());
                });
    }

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public void login(String email, String password, LoginCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onLoginSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onLoginFailed("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    public void handleGoogleSignInResult(Intent data, LoginWithGoogleCallback callback) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
            if (account != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                callback.onLoginWithGoogleSuccess(mAuth.getCurrentUser());
                            } else {
                                callback.onLoginWithGoogleFailed("Authentication failed: " + task.getException().getMessage());
                            }
                        });
            } else {
                callback.onLoginWithGoogleFailed("Google Sign-In account is null.");
            }
        } catch (ApiException e) {
            callback.onLoginWithGoogleFailed("Google Sign-In failed: " + e.getMessage());
        }
    }

    public void saveUserToDatabase(String userId, String email, String username, String imageUrl, SaveUserCallback callback) {
        DatabaseReference usersRef = mDatabase.getReference("users");
        User newUser = new User(userId, email, username, imageUrl);
        usersRef.child(userId).setValue(newUser)
                .addOnSuccessListener(aVoid -> {
                    callback.onSaveUserSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onSaveUserFailed("Failed to save user data: " + e.getMessage());
                });
    }

    public void signInAsGuest(LoginCallback callback) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInAnonymously:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        callback.onLoginSuccess(user);
                    } else {
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        callback.onLoginFailed(task.getException().getMessage());
                    }
                });
    }

    public interface SaveUserCallback {
        void onSaveUserSuccess();
        void onSaveUserFailed(String errorMessage);
    }

    public interface LoginWithGoogleCallback {
        void onLoginWithGoogleSuccess(FirebaseUser user);
        void onLoginWithGoogleFailed(String errorMessage);
    }

    public interface LoginCallback {
        void onLoginSuccess(FirebaseUser user);
        void onLoginFailed(String errorMessage);
    }

    interface UpdateUserCallback {
        void onUpdateUserSuccess();
        void onUpdateUserFailed(String errorMessage);
    }

    interface SignupCallback {
        void onSignupSuccess();
        void onSignupFailed(String errorMessage);
    }

    public interface UploadCallback {
        void onUploadSuccess(String imageUrl);
        void onUploadFailed(String errorMessage);
    }

    public interface GetUserCallback {
        void onGetUserSuccess(User user);
        void onGetUserFailed(String errorMessage);
    }
}