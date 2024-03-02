package com.abdulhameed.foodieplan.home.plan.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.databinding.DialogSelectDayBinding;
import com.abdulhameed.foodieplan.databinding.FragmentPlanBinding;
import com.abdulhameed.foodieplan.home.plan.PlanContract;
import com.abdulhameed.foodieplan.home.plan.presenter.PlanPresenter;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.PlannedMeal;
import com.abdulhameed.foodieplan.model.factory.MealRepositoryFactory;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.utils.MyCalender;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class PlanFragment extends Fragment implements PlanContract.View {

    private PlanAdapter adapter;
    private PlanContract.Presenter presenter;
    FragmentPlanBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new PlanPresenter(this, MealRepositoryFactory.getInstance(requireContext()).createMealRepository(),
                FavouriteRepository.getInstance(), SharedPreferencesManager.getInstance(requireContext()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlanBinding.inflate(getLayoutInflater(), container, false);

        initRecyclerView();
        presenter.getPlannedMeals();

        return binding.getRoot();
    }

    private void initRecyclerView() {
        adapter = new PlanAdapter(new PlanAdapter.OnPlannedClickListener<PlannedMeal>() {
            @Override
            public void onItemClick(PlannedMeal meal) {
                PlanFragmentDirections.ActionPlanFragmentToDetailsFragment direction =
                        PlanFragmentDirections.actionPlanFragmentToDetailsFragment(meal.getId());
                Navigation.findNavController(binding.getRoot()).navigate(direction);
            }

            @Override
            public void onFavouriteClick(PlannedMeal meal) {
                presenter.addFavourite(meal);
            }

            @Override
            public void onPlanClick(PlannedMeal plannedMeal) {
                showDaySelectionDialog(plannedMeal);
            }

            @Override
            public void onDeleteClick(PlannedMeal meal) {
                presenter.deleteMeal(meal);
            }
        });
        binding.rvPlan.setAdapter(adapter);
    }

    @Override
    public void showMessage(String error) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPlannedMeals(List<PlannedMeal> plannedMeals) {
        if (plannedMeals != null&& plannedMeals.size() != 0) {
            adapter.submitList(plannedMeals);
        } else {
            binding.avNoDataFound.setVisibility(View.VISIBLE);
            binding.rvPlan.setVisibility(View.GONE);
        }
    }

    @Override
    public void mealDeleted(PlannedMeal meal) {
        adapter.deleteMeal(meal);
        Snackbar.make(requireView(), "Meal " + meal.getName() + " Removed.", Snackbar.LENGTH_LONG)
                .setAction("Undo", view -> {
                    presenter.addMeal(meal);
                }).show();
    }

    @Override
    public void mealInserted(PlannedMeal meal) {
        adapter.insertMeal(meal);
        Snackbar.make(requireView(), "Meal " + meal.getName() + " Inserted Again!", Snackbar.LENGTH_LONG)
                .setAction("Redo", view -> {
                    presenter.deleteMeal(meal);
                }).show();
    }

    private void showDaySelectionDialog(PlannedMeal meal) {
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

    private void setSelectDays(DialogSelectDayBinding dialogBinding, PlannedMeal meal) {
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