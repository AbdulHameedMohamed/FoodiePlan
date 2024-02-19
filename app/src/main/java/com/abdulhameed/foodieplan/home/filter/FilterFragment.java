package com.abdulhameed.foodieplan.home.filter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.databinding.FragmentFilterBinding;
import com.abdulhameed.foodieplan.home.home.OnClickListener;
import com.abdulhameed.foodieplan.model.data.Category;
import com.abdulhameed.foodieplan.model.data.Country;
import com.abdulhameed.foodieplan.model.data.Ingredient;
import com.abdulhameed.foodieplan.model.data.FilterMeal;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.FilterRepository;
import com.abdulhameed.foodieplan.utils.NetworkManager;

import java.util.List;

public class FilterFragment extends Fragment implements FilterContract.FilterView, OnClickListener<FilterMeal> {

    private FragmentFilterBinding binding;
    private FilterContract.FilterPresenter presenter;
    private FilterAdapter adapter;
    private NavController navController;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter =new FilterPresenter(this,
                FilterRepository.getInstance(MealsRemoteDataSource.getInstance()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFilterBinding.inflate(inflater, container, false);
        adapter = new FilterAdapter(this);
        binding.rvFilterMeals.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(binding.getRoot());

        if (NetworkManager.isOnline(requireContext())) {
            binding.svFilter.setVisibility(View.VISIBLE);
            binding.avNoInternet.setVisibility(View.GONE);

            getArgs();
        } else {
            binding.svFilter.setVisibility(View.GONE);
            binding.avNoInternet.setVisibility(View.VISIBLE);
        }
    }

    private void getArgs() {
        Bundle args = getArguments();
        if (args != null) {
            String filter = args.getString("filter_type");

            if (filter != null) {
                if(filter.equals("Ingredient")) {
                    Ingredient ingredient = (Ingredient) args.getSerializable("filter_value");
                    presenter.getMealsByIngredient(ingredient.getName());
                } else if(filter.equals("Country")) {
                    Country country = (Country) args.getSerializable("filter_value");
                    presenter.getMealsByCountry(country.getName());
                } else {
                    Category category = (Category) args.getSerializable("filter_value");
                    presenter.getMealsByCategory(category.getName());
                }
            }
        }
    }

    @Override
    public void showMeals(List<FilterMeal> meals) {
        stopShimmer();
        adapter.setList(meals);
    }

    @Override
    public void showEmptyDataMessage() {
        stopShimmer();
    }

    @Override
    public void showErrorMsg(String error) {
        stopShimmer();
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
    }

    private void stopShimmer() {
        binding.shRvFilter.stopShimmer();
        binding.shRvFilter.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(FilterMeal meal) {
        FilterFragmentDirections.ActionFilterFragmentToDetailsFragment direction = FilterFragmentDirections.
                actionFilterFragmentToDetailsFragment(meal.getIdMeal());
        navController.navigate(direction);
    }
}