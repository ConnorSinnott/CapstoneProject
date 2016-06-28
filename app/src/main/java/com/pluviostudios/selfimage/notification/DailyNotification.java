package com.pluviostudios.selfimage.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.mainActivity.MainActivity;

/**
 * Created by Spectre on 6/20/2016.
 */
public class DailyNotification {

    public static void pushNotification(Context context) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(context.getString(R.string.Notification_Title))
                        .setContentText(context.getString(R.string.Notification_Content));

        Intent intent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager managerCompat =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        managerCompat.notify(0, mBuilder.build());

    }


}
