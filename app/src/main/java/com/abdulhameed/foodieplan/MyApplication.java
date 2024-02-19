package com.abdulhameed.foodieplan;

import android.app.Application;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.abdulhameed.foodieplan.work.FetchMealWorker;

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
        PeriodicWorkRequest fetchMealWork = new PeriodicWorkRequest.Builder(
                FetchMealWorker.class,
                1, TimeUnit.DAYS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "fetch_meal_work",
                ExistingPeriodicWorkPolicy.REPLACE,
                fetchMealWork);
    }
}