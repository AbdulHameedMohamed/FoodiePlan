package com.abdulhameed.foodieplan.home.favourite.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.databinding.DialogSelectDayBinding;
import com.abdulhameed.foodieplan.databinding.FragmentFavouriteBinding;
import com.abdulhameed.foodieplan.home.favourite.FavouriteContract;
import com.abdulhameed.foodieplan.home.favourite.presenter.FavouritePresenter;
import com.abdulhameed.foodieplan.home.home.view.MealAdapter;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.abdulhameed.foodieplan.utils.MyCalender;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FavouriteFragment extends Fragment implements FavouriteContract.View, MealAdapter.OnMealClickListener<Meal> {
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
                FavouriteRepository.getInstance(),
                SharedPreferencesManager.getInstance(requireContext()));
        initRecyclerView();
        presenter.getFavouriteMeals();
        return binding.getRoot();
    }

    private void initRecyclerView() {
        adapter = new MealAdapter(this);
        binding.rvMeals.setAdapter(adapter);
    }

    @Override
    public void showData(LiveData<List<Meal>> meals) {
        meals.observe(getViewLifecycleOwner(), mealsList -> {
            if (mealsList != null && !mealsList.isEmpty()) {
                binding.avNoDataFound.setVisibility(View.GONE);
                binding.rvMeals.setVisibility(View.VISIBLE);
                adapter.submitList(mealsList);
            }
        });
    }

    public void deleteMeal(Meal meal) {
        presenter.removeMeal(meal);
    }

    @Override
    public void showNoFavourites() {
        binding.avNoDataFound.setVisibility(View.VISIBLE);
        binding.rvMeals.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void mealDeleted(Meal meal) {
        adapter.removeMeal(meal);
        Snackbar.make(requireView(), "Meal " + meal.getName() + " Removed.", Snackbar.LENGTH_LONG)
                .setAction("Undo", view -> {
                    presenter.addMeal(meal);
                }).show();
    }

    @Override
    public void mealInserted(Meal meal) {
        adapter.insertMeal(meal);
        Snackbar.make(requireView(), "Meal "+ meal.getName() +" Added Again.", Snackbar.LENGTH_LONG)
                .setAction("Redo", view -> {
                    presenter.removeMeal(meal);
                }).show();
    }

    public void displayDialog(Meal meal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Are You Sure to remove Item From Your Favourite ?");

        builder.setPositiveButton("Yes", (dialog, which) -> deleteMeal(meal));

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onItemClick(Meal meal) {
        FavouriteFragmentDirections.ActionFavouriteFragmentToDetailsFragment direction =
                FavouriteFragmentDirections.actionFavouriteFragmentToDetailsFragment(meal.getId());
        Navigation.findNavController(binding.getRoot()).navigate(direction);
    }

    @Override
    public void onFavouriteClick(Meal meal) {
        displayDialog(meal);
    }

    @Override
    public void onPlanClick(Meal meal) {
        showDaySelectionDialog(meal);
    }

    private void showDaySelectionDialog(Meal meal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogSelectDayBinding dialogBinding = DialogSelectDayBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();

        String today = MyCalender.getDayName(MyCalender.getDayOfWeek());

        for (String day : MyCalender.getDaysOfWeek()) {
            Chip chip = new Chip(requireContext());
            chip.setText(day);
            chip.setCheckable(true);
            dialogBinding.chipGroup.addView(chip);
        }

        disableChipsBeforeToday(dialogBinding, today);

        dialogBinding.btnSelectDay.setOnClickListener(view -> {
            setSelectDays(dialogBinding, meal);
            dialog.dismiss();
            Toast.makeText(requireContext(), "Meals ", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void setSelectDays(DialogSelectDayBinding dialogBinding, Meal meal) {
        for (int i = 0; i < dialogBinding.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) dialogBinding.chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                String day = chip.getText().toString();
                presenter.savePlannedMeal(day, meal.getId());
                Toast.makeText(requireContext(),
                        meal.getName() + " Stored Successfully To "+ day
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void disableChipsBeforeToday(DialogSelectDayBinding dialogBinding, String today) {
        for (int i = 0; i < dialogBinding.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) dialogBinding.chipGroup.getChildAt(i);
            if(chip.getText().toString().equals(today))
                break;
            chip.setEnabled(false);
        }
    }
}