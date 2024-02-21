package com.abdulhameed.foodieplan.model.repository;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.abdulhameed.foodieplan.model.data.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

    public void signup(User user, Bitmap profileImg, SignupCallback callback) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            Log.d(TAG, "signup: "+ user);
                            user.setId(firebaseUser.getUid());
                            saveUserData(user, profileImg, callback);
                        } else {
                            callback.onSignupFailed("Failed to create user.");
                        }
                    } else {
                        callback.onSignupFailed("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    private void saveUserData(User user, Bitmap profileImg, SignupCallback callback) {
        DatabaseReference usersRef = mDatabase.getReference("users");
        usersRef.child(user.getId()).setValue(user)
                .addOnSuccessListener(aVoid -> callback.onSignupSuccess(user, profileImg))
                .addOnFailureListener(e -> callback.onSignupFailed("Failed to save user data: " + e.getMessage()));
    }

    public void uploadProfileImage(String userId, Bitmap profileImg, UploadCallback callback) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profileImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference profileImgRef = mStorageRef.child("profile_images/" + userId + ".jpg");

        profileImgRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    profileImgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d(TAG, "uploadProfileImage: Success");
                        String downloadUrl = uri.toString();

                        addImageUrlToUser(userId, downloadUrl, callback);
                    });
                })
                .addOnFailureListener(exception -> {
                    Log.d(TAG, "uploadProfileImage: Failed");
                    callback.onUploadFailed(exception.getMessage());
                });
    }

    private void addImageUrlToUser(String userId, String imageUrl, UploadCallback callback) {
        DatabaseReference usersRef = mDatabase.getReference("users").child(userId);
        usersRef.child("imageUrl").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Image URL added successfully to user");
                    callback.onUploadSuccess();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error adding image URL to user: " + e.getMessage()));
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

    public void signInAsGuest(LoginGuestCallback callback) {
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

    public void linkGuestAccount(String email, String password, LinkGuestCallback callback) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(syncTask -> {
                    if (syncTask.isSuccessful()) {
                        Log.d(TAG, "linkWithCredential:success");
                        FirebaseUser user = syncTask.getResult().getUser();
                        callback.onLinkGuestSuccess(user);
                    } else {
                        Log.w(TAG, "linkWithCredential:failure", syncTask.getException());
                        callback.onLinkGuestFailed("Failed to link guest account: " + syncTask.getException().getMessage());
                    }
                });
    }

    public void downloadUserImage(GetImageCallback callback) {
        StorageReference profileImgRef = mStorageRef.child("profile_images/" + FirebaseAuth.getInstance().getUid() + ".jpg");

        profileImgRef.getDownloadUrl().addOnSuccessListener(uri -> {
            callback.onDownloadImgSuccess(uri.toString());
        }).addOnFailureListener(exception -> {
            callback.onSaveUserFailed(exception.getMessage());
        });
    }

    public interface SaveUserCallback {
        void onSaveUserSuccess();
        void onSaveUserFailed(String errorMessage);
    }
    public interface GetImageCallback {
        void onDownloadImgSuccess(String url);
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

    public interface LoginGuestCallback {
        void onLoginSuccess(FirebaseUser user);
        void onLoginFailed(String errorMessage);
    }

    interface UpdateUserCallback {
        void onUpdateUserSuccess();
        void onUpdateUserFailed(String errorMessage);
    }

    public interface SignupCallback {
        void onSignupSuccess(User user, Bitmap profileImg);
        void onSignupFailed(String errorMessage);
    }

    public interface UploadCallback {
        void onUploadSuccess();
        void onUploadFailed(String errorMessage);
    }

    public interface GetUserCallback {
        void onGetUserSuccess(User user);
        void onGetUserFailed(String errorMessage);
    }

    public interface LinkGuestCallback {
        void onLinkGuestSuccess(FirebaseUser user);
        void onLinkGuestFailed(String errorMessage);
    }
}