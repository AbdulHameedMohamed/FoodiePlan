package com.abdulhameed.foodieplan.model.repository;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.abdulhameed.foodieplan.model.Meal;
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
import java.util.ArrayList;
import java.util.List;

public class AuthenticationRepository {
    private final FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private StorageReference mStorageRef;
    private static final String TAG = "AuthenticationRepository";

    public AuthenticationRepository(FirebaseAuth mAuth, FirebaseDatabase mDatabase, StorageReference mStorageRef) {
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
        this.mStorageRef = mStorageRef;
    }

    public AuthenticationRepository(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public void signup(User user, Bitmap profileImg, SignupCallback callback) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            Log.d(TAG, "signup: " + user);
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
        mAuth.signOut();
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
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onLoginWithGoogleSuccess(mAuth.getCurrentUser());
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            callback.onLoginWithGoogleFailed("Authentication failed: " + task.getException().getMessage());
                        }
                    });
        } catch (ApiException e) {
            Log.w("TAG", "signInWithCredential:failure"+ e.getMessage());
            callback.onLoginWithGoogleFailed("Google Sign-In failed: " + e.getMessage());
        }
    }

    public void signInAsGuest(LoginGuestCallback callback) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInAnonymously:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        callback.onGuestLoginSuccess(user);
                    } else {
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        callback.onLoginFailed(task.getException().getMessage());
                    }
                });
    }

    public void linkGuestAccount(User user, LinkGuestCallback callback) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), user.getPassword());
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(syncTask -> {
                    if (syncTask.isSuccessful()) {
                        Log.d(TAG, "linkWithCredential:success");
                        FirebaseUser firebaseUser = syncTask.getResult().getUser();
                        callback.onLinkGuestSuccess(firebaseUser);
                    } else {
                        Log.w(TAG, "linkWithCredential:failure", syncTask.getException());
                        callback.onLinkGuestFailed("Failed to link guest account: " + syncTask.getException().getMessage());
                    }
                });
    }

    public void mergeFavoritesFromAnonymousUser(User newUser, String anonymousId) {
        // Get the IDs of both the anonymous user and the authenticated user
        String authenticatedUserId = newUser.getId();

        Log.d(TAG, "mergeFavoritesFromAnonymousUser: " + authenticatedUserId +"  " + anonymousId);
        // Retrieve favorites data from anonymous user
        DatabaseReference anonymousFavoritesRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(anonymousId).child("meals");

        anonymousFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve favorites data from anonymous user
                List<Meal> anonymousFavorites = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Meal favorite = snapshot.getValue(Meal.class);
                    anonymousFavorites.add(favorite);
                }

                // Update favorites for the new user
                DatabaseReference newUserFavoritesRef = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(authenticatedUserId).child("meals");
                newUserFavoritesRef.setValue(anonymousFavorites)
                        .addOnSuccessListener(aVoid -> {
                            // Merge successful
                            // Optionally clean up data associated with anonymous user
                            cleanUpAnonymousUserData();
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle cancelled event
            }
        });
    }

    private void cleanUpAnonymousUserData() {
        DatabaseReference anonymousUserRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child("anonymousUserID");

        anonymousUserRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Clean-up successful
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
    public void getUserData(String userId, final UserDataListener callback) {
        mDatabase.getReference().child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User data found, retrieve it
                    String userEmail = dataSnapshot.child("email").getValue(String.class);
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    String userProfileUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String password = dataSnapshot.child("password").getValue(String.class);
                    User user = new User(userId, userEmail, userName, userProfileUrl);
                    Log.d(TAG, "onDataChange: " + user);
                    Log.d(TAG, "onDataChange: " + userProfileUrl);
                    Log.d(TAG, "onDataChange: " + password);
                    // Pass user data to listener
                    callback.onUserDataReceived(user);
                } else {
                    // User data not found
                    callback.onUserDataCancelled("User Data Not Found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                callback.onUserDataCancelled(databaseError.getMessage());
            }
        });
    }

    // Interface for callback listeners
    public interface UserDataListener {
        void onUserDataReceived(User user);
        void onUserDataCancelled(String errorMessage);
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
        void onGuestLoginSuccess(FirebaseUser user);

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