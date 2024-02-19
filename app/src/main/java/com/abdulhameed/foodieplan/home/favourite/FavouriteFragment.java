package com.abdulhameed.foodieplan.home.favourite;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.databinding.FragmentFavouriteBinding;
import com.abdulhameed.foodieplan.home.home.MealAdapter;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.abdulhameed.foodieplan.utils.NetworkManager;

import java.util.List;

public class FavouriteFragment extends Fragment implements FavouriteContract.View {
    private FragmentFavouriteBinding binding;
    private MealAdapter adapter;
    FavouriteContract.Presenter presenter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        presenter = new FavouritePresenter(this, MealRepository.getInstance(
                MealsRemoteDataSource.getInstance(),
                MealsLocalDataSource.getInstance(getContext())),
                FavouriteRepository.getInstance());
        initRecyclerView();
        presenter.getFavouriteMeals();
        return binding.getRoot();
    }

    private void initRecyclerView() {
        adapter = new MealAdapter(meal -> {
            displayDialog(meal);
            Toast.makeText(requireContext(), meal.getName(), Toast.LENGTH_SHORT).show();
        });
        binding.rvMeals.setAdapter(adapter);
    }

    @Override
    public void showData(LiveData<List<Meal>> meals) {
        meals.observe(getViewLifecycleOwner(), mealsList -> {
            if (mealsList != null && !mealsList.isEmpty()) {
                binding.avNoDataFound.setVisibility(View.GONE);
                binding.rvMeals.setVisibility(View.VISIBLE);
                adapter.submitList(mealsList);
            } else {
                if (!NetworkManager.isOnline(requireContext()))
                    return;

                startShimmer();
                presenter.getFavouriteMealsFromRemote(SharedPreferencesManager.getInstance(requireContext()).getUserId());
            }
        });
    }

    private void startShimmer() {

    }

    @Override
    public void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void deleteMeal(Meal meal) {
        String userId = SharedPreferencesManager.getInstance(requireContext()).getUserId();
        presenter.removeFavouriteMeal(userId, meal);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void afterRemove() {
        presenter.getFavouriteMeals();
    }

    @Override
    public void showNoFavourites() {
        binding.avNoDataFound.setVisibility(View.VISIBLE);
        binding.rvMeals.setVisibility(View.GONE);
    }

    public void displayDialog(Meal meal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are You Sure to remove Item From Your Favourite ?");
        builder.setMessage("");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteMeal(meal);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}