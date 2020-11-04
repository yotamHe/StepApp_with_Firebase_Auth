package com.example.StepApp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("test","test");
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context
                        .NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, NotificationActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100
                , repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle("It's Time to Get Going!")
                .setContentText("You've been Idle for a While now, It's time to get going!")
                .setAutoCancel(true);

        notificationManager.notify(100, builder.build());
    }

}
