package com.sadi.hydrobeacon.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.sadi.hydrobeacon.MainActivity;
import com.sadi.hydrobeacon.R;

public class NotificationHelper {

    private static final String HIGH_CHANNEL_ID = "flood_alerts_channel";
    private static final String LOW_CHANNEL_ID = "low_level_channel";
    
    private static final int HIGH_NOTIFICATION_ID = 101;
    private static final int LOW_NOTIFICATION_ID = 102;

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // High Level Channel
            NotificationChannel highChannel = new NotificationChannel(
                    HIGH_CHANNEL_ID,
                    "Flood Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            highChannel.setDescription("Critical notifications for high water levels");
            highChannel.enableVibration(true);
            
            // Low Level Channel
            NotificationChannel lowChannel = new NotificationChannel(
                    LOW_CHANNEL_ID,
                    "Low Level Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            lowChannel.setDescription("Notifications for critically low water levels");

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build();
            
            Uri alarmSound = Settings.System.DEFAULT_NOTIFICATION_URI;
            highChannel.setSound(alarmSound, audioAttributes);
            lowChannel.setSound(alarmSound, audioAttributes);

            notificationManager.createNotificationChannel(highChannel);
            notificationManager.createNotificationChannel(lowChannel);
        }
    }

    public void sendFloodAlert(int waterLevel, String thresholdInfo) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        int pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntentFlags |= PendingIntent.FLAG_IMMUTABLE;
        }
        
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlags);

        String title = "CRITICAL: High Water Level";
        String summary = "Water level at " + waterLevel + "% " + thresholdInfo;
        String detail = "HydroBeacon detected water level at " + waterLevel + "%. " +
                "This exceeds the configured danger threshold. Please check the situation immediately.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, HIGH_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(summary)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(detail))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 200, 500})
                .setContentIntent(pendingIntent);

        notificationManager.notify(HIGH_NOTIFICATION_ID, builder.build());
    }

    public void sendLowLevelAlert(int waterLevel, String thresholdInfo) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        int pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntentFlags |= PendingIntent.FLAG_IMMUTABLE;
        }
        
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlags);

        String detail = "HydroBeacon detected water level at " + waterLevel + "%. " +
                "This is below the configured " + thresholdInfo + ".";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, LOW_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("WARNING: Low Water Level")
                .setContentText("Water level is critically low: " + waterLevel + "%")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(detail))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(LOW_NOTIFICATION_ID, builder.build());
    }
}
