package com.abdulhameed.foodieplan.home.search.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.databinding.DialogSelectDayBinding;
import com.abdulhameed.foodieplan.databinding.FragmentSearchBinding;
import com.abdulhameed.foodieplan.home.favourite.view.FavouriteFragmentDirections;
import com.abdulhameed.foodieplan.home.home.view.MealAdapter;
import com.abdulhameed.foodieplan.home.search.SearchContract;
import com.abdulhameed.foodieplan.home.search.presenter.SearchPresenter;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.abdulhameed.foodieplan.utils.MyCalender;
import com.abdulhameed.foodieplan.utils.NetworkManager;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SearchFragment extends Fragment implements SearchContract.View , MealAdapter.OnMealClickListener<Meal> {

    private FragmentSearchBinding binding;
    private MealAdapter adapter;
    private SearchContract.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SearchPresenter(this,
                MealRepository.getInstance(
                        MealsRemoteDataSource.getInstance(),
                        MealsLocalDataSource.getInstance(getContext())),
                FavouriteRepository.getInstance(),
                SharedPreferencesManager.getInstance(requireContext()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        if (NetworkManager.isOnline(requireContext())) {
            binding.scrollSearch.setVisibility(View.VISIBLE);
            binding.avNoInternet.setVisibility(View.GONE);
        } else {
            binding.scrollSearch.setVisibility(View.GONE);
            binding.avNoInternet.setVisibility(View.VISIBLE);
        }

        initRecyclerView();
        addListeners();

        return binding.getRoot();
    }

    private void initRecyclerView() {
        adapter = new MealAdapter(this);
        binding.rvMeals.setAdapter(adapter);
    }

    private void addListeners() {
        binding.etSearch.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence searchText, int i, int i1, int i2) {
                binding.shRvMeals.setVisibility(View.VISIBLE);
                binding.shRvMeals.startShimmer();
                String search = searchText.toString().toLowerCase();
                presenter.searchFilterItem(search);
            }
        });
    }

    @Override
    public void showMeals(List<Meal> mealsItems) {
        stopShimmer();
        adapter.submitList(mealsItems);
    }

    private void stopShimmer() {
        binding.shRvMeals.stopShimmer();
        binding.shRvMeals.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEmptyDataMessage() {
        Toast.makeText(requireContext(), "SomeThing Wrong Happen", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMsg(String error) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
    }

    public abstract static class TextWatcherAdapter implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    @Override
    public void onItemClick(Meal meal) {
        SearchFragmentDirections.ActionSearchFragmentToDetailsFragment direction =
                SearchFragmentDirections.actionSearchFragmentToDetailsFragment(meal.getId());
        Navigation.findNavController(binding.getRoot()).navigate(direction);
    }

    @Override
    public void onFavouriteClick(Meal meal) {
        presenter.addToFavourite(FirebaseAuth.getInstance().getUid(), meal);
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