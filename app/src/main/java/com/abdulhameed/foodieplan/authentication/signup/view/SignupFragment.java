package com.abdulhameed.foodieplan.authentication.signup.view;

import android.app.Activity;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.authentication.signup.SignupContract;
import com.abdulhameed.foodieplan.authentication.signup.presenter.SignupPresenter;
import com.abdulhameed.foodieplan.databinding.FragmentSignupBinding;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.factory.AuthenticationRepositoryFactory;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.abdulhameed.foodieplan.utils.KeyboardUtils;
import com.abdulhameed.foodieplan.utils.NetworkManager;
import com.squareup.picasso.Picasso;
public class SignupFragment extends Fragment implements SignupContract.View {
    private SignupContract.Presenter presenter;
    private NavController navController;
    private FragmentSignupBinding signupBinding;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SignupPresenter(this,
                AuthenticationRepositoryFactory.getInstance().createAuthenticationRepository(),
                SharedPreferencesManager.getInstance(requireContext()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        signupBinding = FragmentSignupBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);
        setListeners();
        return signupBinding.getRoot();
    }

    private void setListeners() {
        signupBinding.ibEdit.setOnClickListener(view -> openFileChooser());

        signupBinding.btnSignup.setOnClickListener(v -> {
            KeyboardUtils.hideKeyboard(requireContext(), v);
            if(!NetworkManager.isOnline(requireContext())) {
                Toast.makeText(requireContext(), "Check For Your Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap bitmap = getProfileImg();
            String confirmPassword = signupBinding.iConfirmPassword.etPassword.getText().toString();
            presenter.signup(getUser(), confirmPassword, bitmap);
        });

        signupBinding.tvRedirectToLogin.setOnClickListener(v -> navController.navigate(R.id.action_signupFragment_to_loginFragment));
    }

    private Bitmap getProfileImg() {
        Drawable drawable = signupBinding.ivCircular.getDrawable();
        return ((BitmapDrawable) drawable).getBitmap();
    }

    private User getUser() {
        String email = signupBinding.iEmail.etEmail.getText().toString();
        String username = signupBinding.etUsername.getText().toString();
        String password = signupBinding.iPassword.etPassword.getText().toString();

        return new User(email, username, password);
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
        signupBinding.cvSignup.setVisibility(View.GONE);
    }
    @Override
    public void hideProgressBar() {
        signupBinding.avLoading.setVisibility(View.GONE);
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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