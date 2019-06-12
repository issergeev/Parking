package ru.issergeev.parking;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

public class SMSService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        showNotification(intent.getStringExtra("sms_body"));
        return null;
    }

    private void showNotification(String text) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, SplashScreen.class), 1);
        createNotificationChannel();
        Context context = getApplicationContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "qwerty")
                .setContentTitle("Notification")
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.parking_launcher)
                .setAutoCancel(true);
        @SuppressLint("ServiceCast") NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Parking";
            String description = "Desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("qwerty", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String sms_body = intent.getExtras().getString("sms_body");
            showNotification(sms_body);
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }
}