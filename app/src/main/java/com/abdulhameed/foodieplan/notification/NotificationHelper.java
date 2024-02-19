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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("meal_channel", "Meal Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "meal_channel")
                .setSmallIcon(R.drawable.ic_lock)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Set big text style for expanded content
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(content);
        builder.setStyle(bigTextStyle);

        // Show the notification
        notificationManager.notify(1, builder.build());
    }
}