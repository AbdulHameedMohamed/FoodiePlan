package com.abdulhameed.foodieplan.authentication.signup.presenter;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.abdulhameed.foodieplan.authentication.signup.SignupContract;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.utils.SignupValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
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
    private static final String TAG = "SignupPresenter";
    private User uploadedUser;

    private final SharedPreferencesManager preferencesManager;
    public SignupPresenter(SignupContract.View view, FirebaseAuth mAuth,
                           FirebaseDatabase mDatabase, StorageReference mStorageRef, SharedPreferencesManager preferencesManager) {
        this.view = view;
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
        this.mStorageRef = mStorageRef;
        this.preferencesManager = preferencesManager;
    }

    @Override
    public void signup(String email, String userName, String password, String confirmPassword, Bitmap profileImg) {

        if (!SignupValidator.isValidEmail(email)) {
            view.showErrorMessage("Pls Enter Your Email Correct.");
            return;
        }

        if (!SignupValidator.isValidUsername(userName)) {
            view.showErrorMessage("Pls Enter Your UserName Correct.");
            return;
        }

        if (!SignupValidator.isValidPassword(password)) {
            view.showErrorMessage("Pls Enter Your Password Correct.");
            return;
        }

        if (!SignupValidator.isValidPasswordConfirmation(password, confirmPassword)) {
            view.showErrorMessage("Passwords do not match.");
            return;
        }

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
                            if (preferencesManager.isGuest()) {
                                linkGuestAccount(email, password);
                            } else {
                                view.navigateToLogin();
                            }
                        } else {
                            view.showErrorMessage("Failed to create user.");
                        }
                    } else {
                        view.showErrorMessage("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    private void linkGuestAccount(String email, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(syncTask -> {
                    if (syncTask.isSuccessful()) {
                        Log.d(TAG, "linkWithCredential:success");
                        FirebaseUser user = syncTask.getResult().getUser();
                        view.navigateToLogin();
                    } else {
                        Log.w(TAG, "linkWithCredential:failure", syncTask.getException());
                        view.showErrorMessage("Failed to link guest account: " + syncTask.getException().getMessage());
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

                    profileImgRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        String downloadUrl = uri.toString();
                        uploadedUser.setProfileUrl(downloadUrl);

                        saveUserData(uploadedUser);
                    }).addOnFailureListener(exception -> {
                        saveUserData(uploadedUser);
                    });
                })
                .addOnFailureListener(exception -> {
                    saveUserData(uploadedUser);
                });
    }

    private void saveUserData(User user) {
        Log.d("TAG", "saveUserData: " + user);
        DatabaseReference usersRef = mDatabase.getReference("users");
        usersRef.child(user.getId()).setValue(user);
    }
}