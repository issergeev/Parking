package ru.issergeev.parking;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

public class SMSService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification(intent.getStringExtra("sms_body"));
        return super.onStartCommand(intent, flags, startId);
    }

    public void showNotification(String text) {
        String title, message;
        if (text.length() == 4) {
            title = getResources().getString(R.string.warning);
            message = getResources().getString(R.string.payment_difficulties);
        } else if (text.equals("No_pay")) {
            title = getString(R.string.no_pay_required);
            message = getString(R.string.no_pay_message);
        } else if (text.equals("End")) {
            title = getString(R.string.end);
            message = getString(R.string.end_message);
        } else if (text.equals("No_sessions")) {
            title = getString(R.string.no_sessions);
            message = getString(R.string.no_sessions_message);
        } else {
            title = getString(R.string.confirmed);
            message = getString(R.string.confirmation_received);
        }
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel_01";
        String channelName = "Confirmation";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.parking_launcher)
                .setTicker(getString(R.string.parking_notification))
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setColor(getResources().getColor(android.R.color.holo_orange_light))
                .setContentText(message);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(new Intent(this, MainPage.class));
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }
}