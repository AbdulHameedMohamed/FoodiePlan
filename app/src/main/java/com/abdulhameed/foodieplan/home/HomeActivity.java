package com.abdulhameed.foodieplan.home;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(!SharedPreferencesManager.getInstance(this).isGuest()) {
            setupNavigationDrawer();
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.topAppBar, R.string.open_nav,
                    R.string.close_nav);
            binding.drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        navController = Navigation.findNavController(this, R.id.home_nav_host);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
    }

    private void setupNavigationDrawer() {
// Set the toolbar as the action bar
        setSupportActionBar(binding.topAppBar);

        User user = SharedPreferencesManager.getInstance(this).getUser();
        setDrawableIcon(user.getProfileUrl());
        View navHeader = binding.navView.getHeaderView(0);
        NavHeaderBinding headerBinding = NavHeaderBinding.bind(navHeader);
        headerBinding.tvUserName.setText(user.getUserName());
        headerBinding.tvEmail.setText(user.getEmail());
        Picasso.get().load(user.getProfileUrl()).into(headerBinding.ivProfile);
// Set up the navigation icon click listener to open the drawer
        binding.topAppBar.setNavigationOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });


        // Set up the navigation item click listener
        binding.navView.setNavigationItemSelectedListener(item -> {
            // Handle navigation item clicks here
            addListeners(item);
            // Close the drawer after handling the click
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private static final String TAG = "HomeActivity";
    private void setDrawableIcon(String profileUrl) {
        ImageLoader.loadCircularBitmap(profileUrl)
                .subscribe(bitmap -> {
                    // Set the circular bitmap as the navigation icon of the MaterialToolbar
                    binding.topAppBar.setNavigationIcon(new BitmapDrawable(getResources(), bitmap));
                }, throwable -> {
                    Log.e(TAG, "Failed to load bitmap from URL: " + profileUrl, throwable);
                });
    }

    private void addListeners(MenuItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}