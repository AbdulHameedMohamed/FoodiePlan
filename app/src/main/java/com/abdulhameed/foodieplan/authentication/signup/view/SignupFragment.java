package com.abdulhameed.foodieplan.authentication.signup.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.authentication.signup.SignupContract;
import com.abdulhameed.foodieplan.authentication.signup.presenter.SignupPresenter;
import com.abdulhameed.foodieplan.databinding.FragmentSignupBinding;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.utils.NetworkManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
public class SignupFragment extends Fragment implements SignupContract.View {
    private SignupContract.Presenter presenter;
    private NavController navController;
    private FragmentSignupBinding signupBinding;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(requireContext());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        presenter = new SignupPresenter(this, mAuth, mDatabase, mStorageRef, SharedPreferencesManager.getInstance(requireContext()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        signupBinding = FragmentSignupBinding.inflate(inflater, container, false);;
        navController = NavHostFragment.findNavController(this);
        addListeners();
        return signupBinding.getRoot();
    }

    private void addListeners() {
        signupBinding.ivCircular.setOnClickListener(view -> {
            openFileChooser();
        });

        signupBinding.btnSignup.setOnClickListener(v -> {
            if(!NetworkManager.isOnline(requireContext())) {
                Toast.makeText(requireContext(), "Check For Your Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = signupBinding.etEmail.getText().toString();
            String username = signupBinding.etUserName.getText().toString();
            String password = signupBinding.etPassword.getText().toString();
            String confirmPassword = signupBinding.etConfirmPassword.getText().toString();
            Drawable drawable = signupBinding.ivCircular.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            presenter.signup(email, username, password, confirmPassword, bitmap);
        });

        signupBinding.tvRedirectToLogin.setOnClickListener(v -> {
            navController.navigate(R.id.action_signupFragment_to_loginFragment);
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void showProgressBar() {
        signupBinding.avLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        signupBinding.avLoading.setVisibility(View.GONE);
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToLogin() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            Uri mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(signupBinding.ivCircular);
        }
    }
}