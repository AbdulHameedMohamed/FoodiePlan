package com.abdulhameed.foodieplan.home.plan.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.databinding.FragmentPlanBinding;
import com.abdulhameed.foodieplan.home.plan.PlanContract;
import com.abdulhameed.foodieplan.home.plan.presenter.PlanPresenter;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.PlannedMeal;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.MealRepository;

import java.util.List;

public class PlanFragment extends Fragment implements PlanContract.View {

    private PlanAdapter adapter;
    private PlanContract.Presenter presenter;
    FragmentPlanBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new PlanPresenter(this, MealRepository.getInstance(MealsRemoteDataSource.getInstance(),
                MealsLocalDataSource.getInstance(requireContext())),
                SharedPreferencesManager.getInstance(requireContext()));
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
        adapter = new PlanAdapter(plannedMeal -> {
            PlanFragmentDirections.ActionPlanFragmentToDetailsFragment direction =
                    PlanFragmentDirections.actionPlanFragmentToDetailsFragment(plannedMeal.getId());
            Navigation.findNavController(binding.getRoot()).navigate(direction);
        });
        binding.rvPlan.setAdapter(adapter);
    }

    @Override
    public void showErrorMessage(String error) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
    }

    private static final String TAG = "PlanFragment";
    @Override
    public void showPlannedMeals(List<PlannedMeal> plannedMeals) {
        Log.d(TAG, "showPlannedMeals: " +plannedMeals.get(0).getDay());
        adapter.submitList(plannedMeals);
    }
}