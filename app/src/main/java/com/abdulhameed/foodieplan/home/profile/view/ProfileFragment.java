package com.abdulhameed.foodieplan.home.profile.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.FragmentProfileBinding;
import com.abdulhameed.foodieplan.home.profile.ProfileContract;
import com.abdulhameed.foodieplan.home.profile.presenter.ProfilePresenter;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment implements ProfileContract.View {
    private ProfileContract.Presenter presenter;
    private FragmentProfileBinding binding;
    NavController navController;
    private static final String TAG = "ProfileFragment";
    BroadcastReceiver nightModeReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navController = NavHostFragment.findNavController(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        setListeners();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (SharedPreferencesManager.getInstance(requireContext()).isGuest()) {
            binding.avSignUp.setVisibility(View.VISIBLE);
            binding.btnGuestSignup.setVisibility(View.VISIBLE);
            binding.clProfile.setVisibility(View.GONE);
            Snackbar.make(requireView(), "Sign Up To Get All The Features From Foodie App", Snackbar.LENGTH_LONG)
                    .setAction("Ok", null).show();
        } else {
            binding.avSignUp.setVisibility(View.GONE);
            binding.btnGuestSignup.setVisibility(View.GONE);
            binding.clProfile.setVisibility(View.VISIBLE);

            presenter = new ProfilePresenter(this,
                    MealRepository.getInstance(MealsRemoteDataSource.getInstance(), MealsLocalDataSource.getInstance(requireContext())),
                    SharedPreferencesManager.getInstance(requireContext()));

            presenter.getFavMealsCount();
            presenter.getPlannedMealsCount();
            presenter.getUser();

            setDrawableImages();
            nightModeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    setDrawableImages();
                }
            };
            registerNightReceiver();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    private void registerNightReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        requireActivity().registerReceiver(nightModeReceiver, filter);
    }

    private void setListeners() {
        binding.ibLogout.setOnClickListener(view -> displayDialog());
        binding.btnGuestSignup.setOnClickListener(view -> {
            presenter.btnSignupClicked();
            navController.navigate(R.id.action_profileFragment_to_mainActivity);
        });
    }

    private void setDrawableImages() {
        Log.d(TAG, "setBackgroundImage: ");
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES;

        if (isDarkMode) {
            setDarkModeImages();
        } else {
            setLightModeImages();
        }
    }

    private void setDarkModeImages() {
        binding.ibLogout.setImageResource(R.drawable.ic_logout_white);
        binding.ivFavourite.setImageResource(R.drawable.ic_red_heart);
        binding.ivPlan.setImageResource(R.drawable.ic_calendar_white);
    }

    private void setLightModeImages() {
        binding.ibLogout.setImageResource(R.drawable.ic_logout_black);
        binding.ivFavourite.setImageResource(R.drawable.ic_black_heart);
        binding.ivPlan.setImageResource(R.drawable.ic_calendar_black);
    }

    @Override
    public void showUserData(User user) {
        if (user.getProfileUrl() != null && !user.getProfileUrl().isEmpty())
            Picasso.get().load(user.getProfileUrl()).into(binding.ivProfile);
        binding.tvName.setText(user.getUserName());
        binding.tvEmail.setText(user.getEmail());
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayImage(String url) {
        Picasso.get().load(url).placeholder(R.drawable.profile).into(binding.ivProfile);
    }

    @Override
    public void displayMealsCount(LiveData<Integer> mealsCount) {
        mealsCount.observe(getViewLifecycleOwner(), count -> binding.tvNumOfFavourites.setText(String.valueOf(count)));
    }

    @Override
    public void showPlannedMealsCount(int countOfDays) {
        binding.tvNumOfPlan.setText(String.valueOf(countOfDays));
    }

    public void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_ask);

        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            presenter.logOut(new AuthenticationRepository(FirebaseAuth.getInstance()));
            navController.navigate(R.id.action_profileFragment_to_mainActivity);
            Toast.makeText(requireContext(), getString(R.string.see_you), Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton(requireActivity().getResources().getResourceEntryName(R.string.no), (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        requireActivity().unregisterReceiver(nightModeReceiver);
        super.onDestroyView();
    }
}