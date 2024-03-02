package com.abdulhameed.foodieplan.home.favourite.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.DialogSelectDayBinding;
import com.abdulhameed.foodieplan.databinding.FragmentFavouriteBinding;
import com.abdulhameed.foodieplan.home.favourite.FavouriteContract;
import com.abdulhameed.foodieplan.home.favourite.presenter.FavouritePresenter;
import com.abdulhameed.foodieplan.home.home.view.MealAdapter;
import com.abdulhameed.foodieplan.model.data.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.abdulhameed.foodieplan.utils.MyCalender;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
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
        adapter = new MealAdapter(this, requireActivity());
        binding.rvMeals.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback());
        itemTouchHelper.attachToRecyclerView(binding.rvMeals);
    }

    @Override
    public void showData(LiveData<List<Meal>> meals) {
        meals.observe(getViewLifecycleOwner(), mealsList -> {
            if (mealsList != null && !mealsList.isEmpty()) {
                binding.avNoDataFound.setVisibility(View.GONE);
                binding.rvMeals.setVisibility(View.VISIBLE);
                adapter.submitList(mealsList);
            } else {
                binding.avNoDataFound.setVisibility(View.VISIBLE);
                binding.rvMeals.setVisibility(View.GONE);
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

    @Override
    public void mealsDeleted(ArrayList<Meal> selectedMeals) {
        adapter.removeMeals(selectedMeals);
        Snackbar.make(requireView(), "Selected Meals Removed Successfully.", Snackbar.LENGTH_LONG)
                .setAction("Undo", view -> presenter.addMeals(selectedMeals)).show();
    }

    @Override
    public void mealsInserted(ArrayList<Meal> selectedMeals) {
        adapter.insertMeals(selectedMeals);
        Snackbar.make(requireView(), "Removed Meals Inserted Again Successfully !", Snackbar.LENGTH_LONG)
                .setAction("Redo", view -> presenter.removeMeals(selectedMeals)).show();
    }
    public void displayDialog(Meal meal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Are You Sure to remove Item From Your Favourite ?");

        builder.setPositiveButton("Yes", (dialog, which) -> deleteMeal(meal));

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
            // Bad thing but iam going back to it later
            adapter.notifyDataSetChanged();
        });

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

    @Override
    public void onMealsSelected(ArrayList<Meal> selectedMeals) {
        presenter.removeMeals(selectedMeals);
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

    public class SwipeToDeleteCallback extends ItemTouchHelper.Callback {
        private final Drawable deleteIcon;
        private final ColorDrawable background;

        public SwipeToDeleteCallback() {
            deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_black);
            background = new ColorDrawable(Color.RED);

        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0, ItemTouchHelper.LEFT); // Only allow left swipe
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            displayDialog(adapter.getMeal(position));
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;

            float swipeThreshold = 0.4f; // Set the threshold to 40% of the screen width
            float thresholdX = recyclerView.getWidth() * swipeThreshold;

            if (dX < -thresholdX) {
                dX = -thresholdX; // Limit the maximum swipe distance
            }

            if (dX < 0) {
                // Swiping to the left
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // Calculate position for delete icon
                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                // Draw delete icon
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(c);
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}