package com.abdulhameed.foodieplan.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.authentication.MainActivity;

public class NotificationHelper {

    private final Context mContext;

    public NotificationHelper(Context context) {
        mContext = context;
    }

    public void showNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("meal_channel", "Meal Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "meal_channel")
                .setSmallIcon(R.drawable.ic_lock)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(1, builder.build());
    }
}