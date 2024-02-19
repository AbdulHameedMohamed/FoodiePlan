package com.abdulhameed.foodieplan.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.home.HomeActivity;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_FoodiePlan);
        setContentView(R.layout.activity_main);

        SharedPreferencesManager.getInstance(this).saveUserId(null);
        String userId = SharedPreferencesManager.getInstance(this).getUserId();

        if (userId != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the result to the fragment
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_nav_host);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}