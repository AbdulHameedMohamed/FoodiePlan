package com.abdulhameed.foodieplan.home.profile.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.authentication.MainActivity;
import com.abdulhameed.foodieplan.databinding.FragmentProfileBinding;
import com.abdulhameed.foodieplan.home.profile.ProfileContract;
import com.abdulhameed.foodieplan.home.profile.presenter.ProfilePresenter;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.abdulhameed.foodieplan.utils.MyCalender;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment implements ProfileContract.View {
    private ProfileContract.Presenter presenter;
    private FragmentProfileBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    NavController navController;
    private static final String TAG = "ProfileFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        navController = NavHostFragment.findNavController(this);
        Log.d(TAG, "onCreate: " + SharedPreferencesManager.getInstance(requireContext()).isGuest());
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

            MealsLocalDataSource.getInstance(getContext()).getMealCount().observe(getViewLifecycleOwner(),
                    count -> binding.tvNumOfFavourites.setText(String.valueOf(count)));
            int count = getPlannedMealsCount();
            binding.tvNumOfPlan.setText(String.valueOf(count));

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            presenter = new ProfilePresenter(this,
                    new AuthenticationRepository(mAuth, mDatabase, mStorageRef),
                    MealRepository.getInstance(MealsRemoteDataSource.getInstance(), MealsLocalDataSource.getInstance(requireContext())),
                    SharedPreferencesManager.getInstance(requireContext()));

            presenter.getUser(SharedPreferencesManager.getInstance(requireContext()).getUser().getId());
            presenter.getDownloadUserImage();
        }
        setListeners();
        return binding.getRoot();
    }

    private int getPlannedMealsCount() {
        int countOfDays = 0;
        String[] daysOfWeek = MyCalender.getDaysOfWeek();
        SharedPreferencesManager preferencesManager = SharedPreferencesManager.getInstance(requireContext());
        for (String day : daysOfWeek)
            if (preferencesManager.getMealIdForDay(day) != null)
                countOfDays++;
        return countOfDays;
    }

    private void setListeners() {
        binding.ibEdit.setOnClickListener(view -> openFileChooser());
        binding.ibLogout.setOnClickListener(view -> displayDialog());
        binding.btnGuestSignup.setOnClickListener(view -> {
            presenter.btnSignupClicked();
            navController.navigate(R.id.action_profileFragment_to_mainActivity);
        });
    }

    @Override
    public void showUserData(User user) {
        if (user.getProfileUrl() != null && !user.getProfileUrl().isEmpty())
            Picasso.get().load(user.getProfileUrl()).into(binding.ivProfile);
        binding.etName.setText(user.getUserName());
        binding.etEmail.setText(user.getEmail());
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayImage(String url) {
        Picasso.get().load(url).placeholder(R.drawable.profile).into(binding.ivProfile);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose Your Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            Uri mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(binding.ivProfile);
        }
    }

    public void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Logout");
        builder.setMessage("Are You Sure that you want to logout ?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            presenter.logOut();
            navController.navigate(R.id.action_profileFragment_to_mainActivity);
            Toast.makeText(requireContext(), "See You Soon !", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}