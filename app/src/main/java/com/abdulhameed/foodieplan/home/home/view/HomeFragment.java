package com.abdulhameed.foodieplan.home.home.view;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.DialogSelectDayBinding;
import com.abdulhameed.foodieplan.databinding.DialogUserProfileBinding;
import com.abdulhameed.foodieplan.databinding.FragmentHomeBinding;
import com.abdulhameed.foodieplan.databinding.LayoutIngredientBinding;
import com.abdulhameed.foodieplan.home.filter.view.FilterMealsAdapter;
import com.abdulhameed.foodieplan.home.home.HomeContract;
import com.abdulhameed.foodieplan.home.home.presenter.HomePresenter;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.Category;
import com.abdulhameed.foodieplan.model.data.Country;
import com.abdulhameed.foodieplan.model.data.Filter;
import com.abdulhameed.foodieplan.model.data.FilterMeal;
import com.abdulhameed.foodieplan.model.data.Ingredient;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.model.data.WatchedMeal;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.FavouriteRepository;
import com.abdulhameed.foodieplan.model.repository.FilterRepository;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.abdulhameed.foodieplan.utils.CountryToApiArgumentMap;
import com.abdulhameed.foodieplan.utils.MyCalender;
import com.abdulhameed.foodieplan.utils.NetworkChangeListener;
import com.abdulhameed.foodieplan.utils.NetworkChangeReceiver;
import com.abdulhameed.foodieplan.utils.NetworkManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeFragment extends Fragment implements HomeContract.View, NetworkChangeListener,
        EasyPermissions.PermissionCallbacks, MealAdapter.OnMealClickListener<Meal> {
    private HomeContract.Presenter presenter;
    private FragmentHomeBinding binding;
    private MealAdapter interestsAdapter;
    private FilterMealsAdapter filterMealsAdapter;
    private FilterAdapter filterAdapter, countriesAdapter, categoriesAdapter;
    private NavController navController;
    private Meal mealOfTheDay;
    private NetworkChangeReceiver networkChangeReceiver;
    private static final String TAG = "HomeFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private String userCountry = "Egypt";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new HomePresenter(
                MealRepository.getInstance(
                        MealsRemoteDataSource.getInstance(),
                        MealsLocalDataSource.getInstance(getContext())),
                FavouriteRepository.getInstance(),
                SharedPreferencesManager.getInstance(requireContext()));
        networkChangeReceiver = new NetworkChangeReceiver(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        presenter.attachView(this);

        return binding.getRoot();
    }

    @AfterPermissionGranted(LOCATION_PERMISSION_REQUEST_CODE)
    private void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(requireContext(), perms)) {
            getCountryFromLocation();
        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your location to get the best experience from it.",
                    LOCATION_PERMISSION_REQUEST_CODE, perms);
        }
    }

    private void getCountryFromLocation() {
        getLastKnownLocation();
    }

    private String getCountryFromLocation(Location location) {
        if (location != null) {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    return addresses.get(0).getCountryName();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsGranted: ");
        Toast.makeText(getContext(), "Granted", Toast.LENGTH_SHORT).show();
        getCountryFromLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        disableCountryMeals();
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: ");
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            String country = getCountryFromLocation(location);
                            if (country != null) {
                                this.userCountry = country;
                                Toast.makeText(requireContext(), "The Virtual Device Location On: " + country, Toast.LENGTH_SHORT).show();
                                getCountryMeals(country);
                            }
                        } else {
                            binding.shRvCountryMeals.stopShimmer();
                            binding.shRvCountryMeals.setVisibility(View.GONE);
                            binding.tvCountryMeals.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private void getCountryMeals(String country) {
        String nationality = CountryToApiArgumentMap.getApiArgumentForCountry(country);
        presenter.getCountryMeals(nationality, FilterRepository.getInstance(MealsRemoteDataSource.getInstance()));
    }

    private void disableCountryMeals() {
        binding.shRvCountryMeals.stopShimmer();
        binding.shRvCountryMeals.setVisibility(View.GONE);
        binding.tvCountryMeals.setVisibility(View.GONE);
        binding.rvCountryMeals.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(binding.getRoot());

        initRecyclerViews();
        setListeners();
        User user = SharedPreferencesManager.getInstance(requireContext()).getUser();
        Picasso.get().load(user.getProfileUrl()).into(binding.ivProfile);
        binding.ivProfile.setOnClickListener(view1 -> openProfileDialog(user));
        handleOnlineStatusAndLoadData();
    }

    private void openProfileDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogUserProfileBinding profileBinding = DialogUserProfileBinding.inflate(getLayoutInflater());
        builder.setView(profileBinding.getRoot());
        AlertDialog dialog = builder.create();

        profileBinding.textViewName.setText("Name: " + user.getUserName());
        profileBinding.textViewEmail.setText("Email: " + user.getEmail());
        Picasso.get().load(user.getProfileUrl()).into(profileBinding.imageViewProfile);

// Show the dialog
        dialog.show();
    }

    private void handleOnlineStatusAndLoadData() {
        if (NetworkManager.isOnline(requireContext())) {
            fetchDataWhenOnline();
        } else {
            showNoInternet();
        }
    }

    private void showNoInternet() {
        binding.svHome.setVisibility(View.GONE);
        binding.avNoInternet.setVisibility(View.VISIBLE);
    }

    private void fetchDataWhenOnline() {
        binding.svHome.setVisibility(View.VISIBLE);
        binding.avNoInternet.setVisibility(View.GONE);
        startShimmer();
        requestLocationPermission();
        presenter.getMealOfTheDay();
        presenter.getIngredients();
        presenter.getCountries();
        presenter.getCategories();
        presenter.getInterestsMeals();
    }

    private void setListeners() {
        binding.ibFavourite.setOnClickListener(view -> {
            presenter.addToFavourite(mealOfTheDay);
            Toast.makeText(requireContext(), "Meal Of Day Added Successfully", Toast.LENGTH_SHORT).show();
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
        binding.shRvCountryMeals.startShimmer();
    }

    private void initRecyclerViews() {
        filterAdapter = new FilterAdapter(item -> {
            showIngredientDialog((Ingredient) item);
            Toast.makeText(requireContext(), item.getName(), Toast.LENGTH_LONG).show();
        });
        binding.rvIngredients.setAdapter(filterAdapter);

        countriesAdapter = new FilterAdapter(item -> goToCountryMeals((Country) item));
        binding.rvCountries.setAdapter(countriesAdapter);

        categoriesAdapter = new FilterAdapter(item -> {
            goToCategoryMeals((Category) item);
        });
        binding.rvCategories.setAdapter(categoriesAdapter);

        interestsAdapter = new MealAdapter(this);
        binding.rvInterestsMeals.setAdapter(interestsAdapter);

        filterMealsAdapter = new FilterMealsAdapter(item -> Toast.makeText(requireContext(), item.getName(), Toast.LENGTH_SHORT).show());
        binding.rvCountryMeals.setAdapter(filterMealsAdapter);
    }

    private void goToCategoryMeals(Category item) {
        HomeFragmentDirections.ActionHomeToFilterFragment action =
                HomeFragmentDirections.actionHomeToFilterFragment("Category", item);
        navController.navigate(action);
    }

    private void goToCountryMeals(Country item) {
        HomeFragmentDirections.ActionHomeToFilterFragment action =
                HomeFragmentDirections.actionHomeToFilterFragment("Country", item);
        navController.navigate(action);
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
                .placeholder(R.drawable.p_chief).into(binding.ivMealImg);
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
        stopShimmer(binding.shRvInterest);
        stopShimmer(binding.shRvCountryMeals);
    }

    @Override
    public void showWatchedMeals(LiveData<List<WatchedMeal>> watchedMealsLD) {
        stopShimmer(binding.shRvInterest);
        watchedMealsLD.observe(getViewLifecycleOwner(), watchedMeals -> {
            if (watchedMeals != null && !watchedMeals.isEmpty()) {
                binding.rvInterestsMeals.setVisibility(View.VISIBLE);
                binding.tvInterestsMeals.setVisibility(View.VISIBLE);
                List<Meal> meals = new ArrayList<>();

                for (WatchedMeal watchedMeal : watchedMeals) {
                    meals.add(new Meal(watchedMeal));
                }

                interestsAdapter.submitList(meals);
            } else {
                binding.shRvInterest.setVisibility(View.GONE);
                binding.rvInterestsMeals.setVisibility(View.GONE);
                binding.tvInterestsMeals.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showCountryMeals(List<FilterMeal> filterMeals) {
        stopShimmer(binding.shRvCountryMeals);
        binding.rvCountryMeals.setVisibility(View.VISIBLE);
        binding.tvCountryMeals.setVisibility(View.VISIBLE);
        filterMealsAdapter.setList(filterMeals);
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
        List<Filter> filters = new ArrayList<>(countries);
        countriesAdapter.setList(filters);
    }

    @Override
    public void showCategory(List<Category> categories) {
        stopShimmer(binding.shRvCategories);

        List<Filter> filters = new ArrayList<>(categories);
        categoriesAdapter.setList(filters);
    }

    private void stopShimmer(ShimmerFrameLayout shimmer) {
        shimmer.stopShimmer();
        shimmer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEmptyDataMessage() {
        stopAllShimmers();
    }

    @Override
    public void deleteMeal(Meal meal) {
        presenter.removeFromFavourite(meal);
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
            presenter.getCountries();
            presenter.getCategories();
            presenter.getInterestsMeals();
            getCountryMeals(userCountry);
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
        presenter.addToFavourite(meal);
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
                        meal.getName() + " Stored Successfully To " + day
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void disableChipsBeforeToday(DialogSelectDayBinding dialogBinding, String today) {
        for (int i = 0; i < dialogBinding.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) dialogBinding.chipGroup.getChildAt(i);
            if (chip.getText().toString().equals(today))
                break;
            chip.setEnabled(false);
        }
    }
}