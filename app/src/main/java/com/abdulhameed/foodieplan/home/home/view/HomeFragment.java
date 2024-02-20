package com.abdulhameed.foodieplan.home.home.view;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.DialogSelectDayBinding;
import com.abdulhameed.foodieplan.databinding.FragmentHomeBinding;
import com.abdulhameed.foodieplan.databinding.LayoutIngredientBinding;
import com.abdulhameed.foodieplan.home.home.HomeContract;
import com.abdulhameed.foodieplan.home.home.presenter.HomePresenter;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.Category;
import com.abdulhameed.foodieplan.model.data.Country;
import com.abdulhameed.foodieplan.model.data.Filter;
import com.abdulhameed.foodieplan.model.data.Ingredient;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.data.WatchedMeal;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.abdulhameed.foodieplan.utils.MyCalender;
import com.abdulhameed.foodieplan.utils.NetworkChangeListener;
import com.abdulhameed.foodieplan.utils.NetworkChangeReceiver;
import com.abdulhameed.foodieplan.utils.NetworkManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements HomeContract.View, NetworkChangeListener, MealAdapter.OnMealClickListener<Meal> {
    private HomeContract.Presenter presenter;
    FragmentHomeBinding binding;
    private MealAdapter mealsAdapter;
    private FilterAdapter filterAdapter;
    private FilterAdapter countriesAdapter;
    private FilterAdapter categoriesAdapter;
    private NavController navController;
    private Meal mealOfTheDay;
    private NetworkChangeReceiver networkChangeReceiver;
    private boolean isFavourite;
    private static final String TAG = "HomeFragment";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: "+ SharedPreferencesManager.getInstance(requireContext()).isGuest());
        presenter = new HomePresenter(
                MealRepository.getInstance(
                        MealsRemoteDataSource.getInstance(),
                        MealsLocalDataSource.getInstance(getContext())),
                FavouriteRepository.getInstance(),
                SharedPreferencesManager.getInstance(requireContext()));
        networkChangeReceiver = new NetworkChangeReceiver(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        presenter.attachView(this);

        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(binding.getRoot());

        initRecyclerViews();
        setListeners();

        if (NetworkManager.isOnline(requireContext())) {
            binding.svHome.setVisibility(View.VISIBLE);
            binding.avNoInternet.setVisibility(View.GONE);
            startShimmer();
            presenter.getMealOfTheDay();
            presenter.getIngredients();
            presenter.getCountry();
            presenter.getCategories();
            presenter.getWatchedMeals();
        } else {
            binding.svHome.setVisibility(View.GONE);
            binding.avNoInternet.setVisibility(View.VISIBLE);
        }
    }

    private void setListeners() {
        binding.ibFavourite.setOnClickListener(view -> {
            if (isFavourite) {
                deleteMeal(mealOfTheDay);
                binding.ibFavourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_red_heart));
            } else {
                addMeal(mealOfTheDay);
                binding.ibFavourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_black_heart));
            }
            isFavourite = !isFavourite;
        });
        binding.cvMealOfTheDay.setOnClickListener(view -> {
            HomeFragmentDirections.ActionHomeFragmentToDetailsFragment direction = HomeFragmentDirections.
                    actionHomeFragmentToDetailsFragment(mealOfTheDay.getId());
            Navigation.findNavController(binding.getRoot()).navigate(direction);
        });
    }

    private void startShimmer() {
        binding.shRvIngredients.startShimmer();
        binding.shRvCountries.startShimmer();
        binding.shRvCategories.startShimmer();
        binding.shCvMeal.startShimmer();
        binding.shRvInterest.startShimmer();
    }

    private void initRecyclerViews() {
        filterAdapter = new FilterAdapter(item -> {
            showIngredientDialog((Ingredient) item);
            Toast.makeText(requireContext(), item.getName(), Toast.LENGTH_LONG).show();
        });
        binding.rvIngredients.setAdapter(filterAdapter);

        countriesAdapter = new FilterAdapter(item -> {
            HomeFragmentDirections.ActionHomeToFilterFragment action =
                    HomeFragmentDirections.actionHomeToFilterFragment("Country", (Country) item);
            navController.navigate(action);
        });
        binding.rvCountries.setAdapter(countriesAdapter);

        categoriesAdapter = new FilterAdapter(item -> {
            HomeFragmentDirections.ActionHomeToFilterFragment action =
                    HomeFragmentDirections.actionHomeToFilterFragment("Category", (Category) item);
            navController.navigate(action);
            Toast.makeText(requireContext(), item.getName(), Toast.LENGTH_LONG).show();
        });
        binding.rvCategories.setAdapter(categoriesAdapter);

        mealsAdapter = new MealAdapter(this);
        binding.rvCountryMeals.setAdapter(mealsAdapter);
    }

    @Override
    public void showMealOfTheDay(Meal meal) {
        stopShimmer(binding.shCvMeal);
        this.mealOfTheDay = meal;
        binding.cvMealOfTheDay.setVisibility(View.VISIBLE);
        binding.tvName.setText(meal.getName());
        binding.tvCategoryMeal.setText(meal.getCategory());
        binding.tvCountryMeal.setText(meal.getCountry());
        Picasso.get().load(meal.getThumb())
                .placeholder(R.drawable.ic_cooking).into(binding.ivMealImg);
    }

    @Override
    public void showError(String message) {
        stopAllShimmers();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void stopAllShimmers() {
        stopShimmer(binding.shCvMeal);
        stopShimmer(binding.shRvIngredients);
        stopShimmer(binding.shRvCategories);
        stopShimmer(binding.shRvCountries);
    }

    @Override
    public void showWatchedMeals(LiveData<List<WatchedMeal>> watchedMealsLD) {
        stopShimmer(binding.shRvInterest);
        binding.shRvInterest.setVisibility(View.INVISIBLE);
        watchedMealsLD.observe(getViewLifecycleOwner(), watchedMeals -> {
            if (watchedMeals != null && !watchedMeals.isEmpty()) {
                List<Meal> meals = new ArrayList<>();

                for (WatchedMeal watchedMeal : watchedMeals) {
                    meals.add(new Meal(watchedMeal.getId(), watchedMeal.getName(), watchedMeal.getCategory(), watchedMeal.getCountry(), watchedMeal.getThumb()));
                }

                mealsAdapter.submitList(meals);
            } else {
                binding.rvCountryMeals.setVisibility(View.GONE);
                binding.tvInterestsMeals.setVisibility(View.GONE);
            }
        });
    }

    private String getCurrentCountry() {
        // Get the default locale
        Locale locale = getResources().getConfiguration().locale;
        // Get the country name from the locale
        return locale.getDisplayCountry();
    }

    @Override
    public void showIngredients(List<Ingredient> ingredients) {
        stopShimmer(binding.shRvIngredients);

        List<Filter> filters = new ArrayList<>(ingredients);

        filterAdapter.setList(filters);
    }

    @Override
    public void showCountry(List<Country> countries) {
        stopShimmer(binding.shRvCountries);
        List<Filter> filters = new ArrayList<>();

        for (Country country: countries) {
            filters.add(country);
        }
        countriesAdapter.setList(filters);
    }

    @Override
    public void showCategory(List<Category> categories) {
        stopShimmer(binding.shRvCategories);
        List<Filter> filters = new ArrayList<>();

        for (Category category: categories) {
            filters.add(category);
        }
        categoriesAdapter.setList(filters);
    }

    private void stopShimmer(ShimmerFrameLayout shimmer) {
        shimmer.stopShimmer();
        shimmer.setVisibility(View.INVISIBLE);
    }
    @Override
    public void addMeal(Meal meal) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        presenter.addToFavourite(userId, meal);
    }
    @Override
    public void showEmptyDataMessage() {
        stopAllShimmers();
    }

    @Override
    public void deleteMeal(Meal meal) {
        presenter.removeFromFavourite(SharedPreferencesManager.getInstance(requireContext()).getUserId(), meal);
    }

    private void showIngredientDialog(Ingredient ingredient) {

        LayoutIngredientBinding ingredientBinding = LayoutIngredientBinding.inflate(getLayoutInflater());
        ingredientBinding.tvIngredientName.setText(ingredient.getName());
        ingredientBinding.tvIngredientDescription.setText(ingredient.getDescription());

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(ingredientBinding.getRoot());
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            HomeFragmentDirections.ActionHomeToFilterFragment action =
                    HomeFragmentDirections.actionHomeToFilterFragment("Ingredient", ingredient);
            navController.navigate(action);
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerNetworkChangeReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterNetworkChangeReceiver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
        isFavourite = false;
    }

    private void registerNetworkChangeReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        requireActivity().registerReceiver(networkChangeReceiver, filter);
    }

    private void unregisterNetworkChangeReceiver() {
        requireActivity().unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        if (isConnected) {
            binding.svHome.setVisibility(View.VISIBLE);
            binding.avNoInternet.setVisibility(View.GONE);
            startShimmer();
            presenter.getMealOfTheDay();
            presenter.getIngredients();
            presenter.getCountry();
            presenter.getCategories();
        } else {
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(Meal meal) {
        HomeFragmentDirections.ActionHomeFragmentToDetailsFragment direction = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(meal.getId());
        Navigation.findNavController(binding.getRoot()).navigate(direction);
    }

    @Override
    public void onFavouriteClick(Meal meal) {
        addMeal(meal);
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