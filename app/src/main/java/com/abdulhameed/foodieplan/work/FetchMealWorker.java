package com.abdulhameed.foodieplan.work;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.remote.NetworkCallBack;
import com.abdulhameed.foodieplan.utils.NotificationHelper;
import com.abdulhameed.foodieplan.utils.MyCalender;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FetchMealWorker extends Worker implements NetworkCallBack<List<Meal>> {
    private static final String TAG = "FetchMealWorker";
    private final NotificationHelper notificationHelper;

    public FetchMealWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        notificationHelper = new NotificationHelper(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        MealsRemoteDataSource apiService = MealsRemoteDataSource.getInstance();
        apiService.getRandomMeal(this);
        SharedPreferencesManager preferencesManager = SharedPreferencesManager.getInstance(getApplicationContext());

        int dayOfWeek = MyCalender.getDayOfWeek();

        String dayName = MyCalender.getDayName(dayOfWeek);
        String yesterdayName = MyCalender.getDayName(dayOfWeek-1);

        preferencesManager.saveMealIdForDay(yesterdayName, null);
        String mealId = preferencesManager.getMealIdForDay(dayName);
        if(mealId!= null) {
            apiService.getMealById(new NetworkCallBack<Meal>() {
                @Override
                public void onSuccess(Meal plannedMeal) {
                    notificationHelper.showNotification("Today is: "+ dayName + " and your meal to this day is ready!", dayName + "'s meal: " + plannedMeal.getName());
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "onFailure: " + message);
                }
            }, mealId);
        }

        return Result.success();
    }

    @Override
    public void onSuccess(List<Meal> mealOfTheDay) {
        onMealFetched(mealOfTheDay);
    }

    private void onMealFetched(List<Meal> mealOfTheDay) {
        if (mealOfTheDay != null && !mealOfTheDay.isEmpty()) {
            Meal meal = mealOfTheDay.get(0);
            SharedPreferencesManager.getInstance(getApplicationContext()).saveMealOfTheDay(meal);
            notificationHelper.showNotification("Good Morning Sir!\nYour meal of the day is ready!", "Today's meal: " + meal.getName());
        }
    }

    @Override
    public void onFailure(String message) {
        Log.d(TAG, "onFailure: " + message);
    }

    private void scheduleNextWorkRequest() {
        OneTimeWorkRequest fetchMealWork = new OneTimeWorkRequest.Builder(FetchMealWorker.class)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(fetchMealWork);
    }
}