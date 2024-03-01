package com.abdulhameed.foodieplan.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.home.HomeActivity;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.User;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        SharedPreferencesManager preferencesManager = SharedPreferencesManager.getInstance(this);

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

        setContentView(R.layout.activity_main);

        if (preferencesManager.getUser() != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_nav_host);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}