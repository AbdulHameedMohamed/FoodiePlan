package com.abdulhameed.foodieplan.authentication.signup;

import android.graphics.Bitmap;
import android.util.Log;

import com.abdulhameed.foodieplan.model.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class SignupPresenter implements SignupContract.Presenter {
    private final SignupContract.View view;
    private final FirebaseAuth mAuth;
    private final FirebaseDatabase mDatabase;
    private final StorageReference mStorageRef;

    private User uploadedUser;

    public SignupPresenter(SignupContract.View view, FirebaseAuth mAuth,
                           FirebaseDatabase mDatabase, StorageReference mStorageRef) {
        this.view = view;
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
        this.mStorageRef = mStorageRef;
    }

    @Override
    public void signup(String email, String userName, String password, String confirmPassword, Bitmap profileImg) {
        view.showProgressBar();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    view.hideProgressBar();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            uploadedUser = new User(user.getUid(), email, userName);
                            uploadProfileImage(user.getUid(), profileImg);
                            saveUserData(uploadedUser);
                            view.navigateToLogin();
                        } else {
                            view.showErrorMessage("Failed to create user.");
                        }
                    } else {
                        view.showErrorMessage("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    private void uploadProfileImage(String userId, Bitmap profileImg) {
        if (profileImg == null) return;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profileImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference profileImgRef = mStorageRef.child("profile_images/" + userId + ".jpg");

        profileImgRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    profileImgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Get the download URL
                        String downloadUrl = uri.toString();
                        uploadedUser.setProfileUrl(downloadUrl);
                        // Store user data with profile image URL in Realtime Database
                        saveUserData(uploadedUser);
                    }).addOnFailureListener(exception -> {
                        saveUserData(uploadedUser);
                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful upload
                    saveUserData(uploadedUser);
                });

    }

    private void saveUserData(User user) {
        Log.d("TAG", "saveUserData: " + user);
        DatabaseReference usersRef = mDatabase.getReference("users");
        usersRef.child(user.getId()).setValue(user);
    }
}