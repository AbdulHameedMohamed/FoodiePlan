package com.abdulhameed.foodieplan.authentication.login.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.abdulhameed.foodieplan.model.factory.AuthenticationRepositoryFactory;
import com.abdulhameed.foodieplan.model.factory.MealRepositoryFactory;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.utils.KeyboardUtils;
import com.abdulhameed.foodieplan.utils.NetworkManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment implements LoginContract.View {
    public static final int RC_SIGN_IN = 123;
    private LoginContract.Presenter presenter;
    NavController navController;
    FragmentLoginBinding loginBinding;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleSignInClient = getGoogleClient();

        presenter = new LoginPresenter.Builder(this)
                .setAuthenticationRepository(AuthenticationRepositoryFactory.getInstance().createAuthenticationRepository())
                .setFavouriteRepository(FavouriteRepository.getInstance())
                .setMealRepository(MealRepositoryFactory.getInstance(requireContext()).createMealRepository())
                .setSharedPreferencesManager(SharedPreferencesManager.getInstance(requireContext()))
                .build();

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
            KeyboardUtils.hideKeyboard(requireContext(), view);
            if (!NetworkManager.isOnline(requireContext())) {
                Toast.makeText(requireContext(), "Check For Your Connection", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = loginBinding.iEmail.etEmail.getText().toString();
            String password = loginBinding.iPassword.etPassword.getText().toString();
            presenter.signInWithEmail(email, password);
        });

        loginBinding.btnGuestLogin.setOnClickListener(view -> presenter.signInAsGuest());

        loginBinding.btnLoginWithGoogle.setOnClickListener(view -> {
            signInWithGoogle();
        });

        loginBinding.tvRedirect.setOnClickListener(v -> {
            navController.navigate(R.id.action_loginFragment_to_signupFragment);
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void showAuthenticationFailedError(String errorMessage) {
        stopLoading();
        Toast.makeText(getContext(), "Authentication failed" + errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHomeActivity(String uid) {
        loginBinding.av.setVisibility(View.GONE);
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
    }

    @Override
    public void showMessage(String errorMessage) {
        stopLoading();
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        loginBinding.av.setVisibility(View.VISIBLE);
        loginBinding.cvLogin.setVisibility(View.GONE);
    }

    @Override
    public void showDialog(String email, String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.guest_mode);
        builder.setMessage(getString(R.string.confirm_guest));

        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            presenter.clearGuest();
            presenter.signInWithEmail(email, password);
        });

        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void stopLoading() {
        loginBinding.av.setVisibility(View.GONE);
        loginBinding.cvLogin.setVisibility(View.VISIBLE);
    }
}