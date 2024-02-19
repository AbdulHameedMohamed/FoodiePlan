package com.abdulhameed.foodieplan.home.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.databinding.FragmentSearchBinding;
import com.abdulhameed.foodieplan.home.home.MealAdapter;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.abdulhameed.foodieplan.utils.NetworkManager;

import java.util.List;

public class SearchFragment extends Fragment implements SearchContract.View {

    private FragmentSearchBinding binding;
    private MealAdapter adapter;
    private SearchContract.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SearchPresenter(this,
                MealRepository.getInstance(
                        MealsRemoteDataSource.getInstance(),
                        MealsLocalDataSource.getInstance(getContext())));
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
        adapter = new MealAdapter(meal -> {
            SearchFragmentDirections.ActionSearchFragmentToDetailsFragment direction = SearchFragmentDirections.
                    actionSearchFragmentToDetailsFragment(meal.getId());
            Navigation.findNavController(binding.getRoot()).navigate(direction);
        });
        binding.rvMeals.setAdapter(adapter);
    }

    private void addListeners() {
        binding.etSearch.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence searchText, int i, int i1, int i2) {
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
}