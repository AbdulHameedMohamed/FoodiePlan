package com.abdulhameed.foodieplan.home;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.ActivityHomeBinding;
import com.abdulhameed.foodieplan.databinding.NavHeaderBinding;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.User;
import com.abdulhameed.foodieplan.utils.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    BroadcastReceiver nightModeReceiver;

    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        nightModeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setBackgroundImage();
            }
        };

        setBackgroundImage();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);

        registerReceiver(nightModeReceiver, filter);

        if (SharedPreferencesManager.getInstance(this).getDarkMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        String countryCode = SharedPreferencesManager.getInstance(this).getLanguage().substring(0, 2);
        Locale locale = new Locale(countryCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration(getResources().getConfiguration());
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        setContentView(binding.getRoot());

        binding.bottomNavigationView.setSelectedItemId(0);
        binding.fabHome.setOnClickListener(view -> {
            binding.bottomNavigationView.setSelectedItemId(-1);
            navController.navigate(R.id.homeFragment);
        });
        if (!SharedPreferencesManager.getInstance(this).isGuest()) {
            setupNavigationDrawer();
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.topAppBar, R.string.open_nav,
                    R.string.close_nav);
            binding.drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        } else {
            binding.topAppBar.setNavigationIcon(R.drawable.ic_guest);
        }

        navController = Navigation.findNavController(this, R.id.home_nav_host);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        int titleTextColor = getResources().getColor(R.color.titleColor);
        binding.topAppBar.setTitleTextColor(titleTextColor);
    }

    private void setBackgroundImage() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES;

        if (isDarkMode)
            getWindow().getDecorView().setBackgroundResource(R.drawable.ic_food_background_dark);
        else
            getWindow().getDecorView().setBackgroundResource(R.drawable.ic_food_background);
    }

    private void setupNavigationDrawer() {
        setSupportActionBar(binding.topAppBar);

        User user = SharedPreferencesManager.getInstance(this).getUser();
        setDrawableIcon(user.getProfileUrl());
        View navHeader = binding.navView.getHeaderView(0);
        NavHeaderBinding headerBinding = NavHeaderBinding.bind(navHeader);
        headerBinding.tvUserName.setText(user.getUserName());
        headerBinding.tvEmail.setText(user.getEmail());
        Picasso.get().load(user.getProfileUrl()).into(headerBinding.ivProfile);

        binding.topAppBar.setNavigationOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.navView.setNavigationItemSelectedListener(item -> {
            setNavigationDrawerListener(item);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private static final String TAG = "HomeActivity";

    private void setDrawableIcon(String profileUrl) {
        ImageLoader.loadCircularBitmap(profileUrl)
                .subscribe(bitmap -> {
                    binding.topAppBar.setNavigationIcon(new BitmapDrawable(getResources(), bitmap));
                }, throwable -> {
                    Log.e(TAG, "Failed to load bitmap from URL: " + profileUrl, throwable);
                });
    }

    private void setNavigationDrawerListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings:
                navController.navigate(R.id.settingsFragment);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(nightModeReceiver);
        super.onDestroy();
    }
}