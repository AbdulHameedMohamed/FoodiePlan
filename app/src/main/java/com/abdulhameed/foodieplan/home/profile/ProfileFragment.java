package com.abdulhameed.foodieplan.home.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.databinding.FragmentProfileBinding;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.AuthenticationRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment implements ProfileContract.View {

    private ProfileContract.Presenter presenter;
    private FragmentProfileBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        MealsLocalDataSource.getInstance(getContext()).getMealCount().observe(getViewLifecycleOwner(),
                count -> {
                    binding.favTv.setText(String.valueOf(count));
                });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        presenter = new ProfilePresenter(this,
                new AuthenticationRepository(mAuth, mDatabase, mStorageRef),
                MealRepository.getInstance(MealsRemoteDataSource.getInstance(), MealsLocalDataSource.getInstance(requireContext())));
        presenter.getUser(SharedPreferencesManager.getInstance(requireContext()).getUserId());
        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
        binding.ivProfile.setOnClickListener(view -> {
            openFileChooser();
        });

        binding.ibLogout.setOnClickListener(view -> {
            presenter.logOut();
            presenter.clearFavourites();
        });
    }
    @Override
    public void showUserData(User user) {
        if(user.getProfileUrl() != null && !user.getProfileUrl().isEmpty())
            Picasso.get().load(user.getProfileUrl()).into(binding.ivProfile);
        binding.etName.setText(user.getUserName());
        binding.etEmail.setText(user.getEmail());
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
}