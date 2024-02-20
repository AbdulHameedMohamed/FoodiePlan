package com.abdulhameed.foodieplan.authentication.login.view;

import android.content.Intent;
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
import com.abdulhameed.foodieplan.authentication.login.LoginContract;
import com.abdulhameed.foodieplan.authentication.login.presenter.LoginPresenter;
import com.abdulhameed.foodieplan.databinding.FragmentLoginBinding;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.abdulhameed.foodieplan.utils.NetworkManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class LoginFragment extends Fragment implements LoginContract.View {
    public static final int RC_SIGN_IN = 123;
    private LoginPresenter presenter;
    NavController navController;
    FragmentLoginBinding loginBinding;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleSignInClient = getGoogleClient();

        presenter = new LoginPresenter(this,
                new AuthenticationRepository(FirebaseAuth.getInstance(),
                        FirebaseDatabase.getInstance(),
                        FirebaseStorage.getInstance().getReference()),
                FavouriteRepository.getInstance(),
                MealRepository.getInstance(MealsRemoteDataSource.getInstance(), MealsLocalDataSource.getInstance(requireContext())),
                SharedPreferencesManager.getInstance(requireContext()));

        navController = NavHostFragment.findNavController(this);
    }

    @NonNull
    private GoogleSignInClient getGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(requireContext(), gso);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false);
        setListeners();
        return loginBinding.getRoot();
    }

    private void setListeners() {
        loginBinding.btnLogin.setOnClickListener(view -> {
            if(!NetworkManager.isOnline(requireContext())) {
                Toast.makeText(requireContext(), "Check For Your Connection", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = loginBinding.etUserName.getText().toString();
            String password = loginBinding.etPassword.getText().toString();
            presenter.signInWithEmail(email, password);
        });

        loginBinding.btnLoginFacebook.setOnClickListener(view -> presenter.signInAsGuest());

        loginBinding.btnLoginGoogle.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        loginBinding.tvRedirect.setOnClickListener(v -> {
            navController.navigate(R.id.action_loginFragment_to_signupFragment);
        });
    }

    @Override
    public void showAuthenticationFailedError(String errorMessage) {
        stopLoading();
        Toast.makeText(getContext(), "Authentication failed" + errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHomeActivity(String uid) {
        SharedPreferencesManager.getInstance(requireContext()).saveUserId(uid);
        presenter.getFavouriteMeals(uid);
        navController.navigate(R.id.action_loginFragment_to_homeActivity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            presenter.handleGoogleSignInResult(data);
        }
    }

    @Override
    public void onGoogleSignInSuccess(FirebaseUser user) {
        navigateToHomeActivity(user.getUid());
        Toast.makeText(getContext(), user.getEmail(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String errorMessage) {
        stopLoading();
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        loginBinding.avLoading.setVisibility(View.VISIBLE);
        loginBinding.cvLogin.setVisibility(View.GONE);
    }

    public void stopLoading() {
        loginBinding.avLoading.setVisibility(View.GONE);
        loginBinding.cvLogin.setVisibility(View.VISIBLE);
    }
}