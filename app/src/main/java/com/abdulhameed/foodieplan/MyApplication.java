package com.abdulhameed.foodieplan;

import android.app.Application;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.abdulhameed.foodieplan.work.AppWorker;
import com.google.firebase.FirebaseApp;

import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        scheduleFetchMealWork();
    }

    private static final String TAG = "MyApplication";
    private void scheduleFetchMealWork() {
        Log.d(TAG, "scheduleFetchMealWork: ");
        FirebaseApp.initializeApp(this);
        PeriodicWorkRequest worker = new PeriodicWorkRequest.Builder(
                AppWorker.class,
                1, TimeUnit.DAYS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "fetch_meal_work",
                ExistingPeriodicWorkPolicy.REPLACE,
                worker);
    }
}