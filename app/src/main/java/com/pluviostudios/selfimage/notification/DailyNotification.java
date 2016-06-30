package com.pluviostudios.selfimage.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.Loader;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.dataContainers.date.DateItemLoaderCallbacksWithDate;
import com.pluviostudios.selfimage.data.database.DatabaseContract;
import com.pluviostudios.selfimage.mainActivity.MainActivity;
import com.pluviostudios.selfimage.utilities.DateUtils;

import java.util.Calendar;

/**
 * Created by Spectre on 6/20/2016.
 */
public class DailyNotification extends BroadcastReceiver
        implements Loader.OnLoadCompleteListener<Cursor>,
        Loader.OnLoadCanceledListener<Cursor> {

    public static final String REFERENCE_ID = "DailyNotification";
    public static final String EXTRA_TEST = "extra_test";
    public static final int NOTIFICATION_ID = 1;

    private Context mContext;
    private Loader mLoader;

    public static void initialize(Context context) {

        boolean useNotifications = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_notification_key), true);

        Intent myIntent = new Intent(context, DailyNotification.class);
        myIntent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (useNotifications) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }

    }

    public static void testNotification(Context context, int seconds) {

        Intent myIntent = new Intent(context, DailyNotification.class);
        myIntent.putExtra(EXTRA_TEST, true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, seconds);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

    }

    public static void pushNotification(Context context) {


        String displayName = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_display_name_key), null);
        displayName = displayName == null ? "" : " " + displayName;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(context.getString(R.string.Notification_Title))
                        .setContentText(context.getString(R.string.Notification_Content, displayName))
                        .setAutoCancel(true);

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

        managerCompat.notify(NOTIFICATION_ID, mBuilder.build());

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;


        long date = DateUtils.getCurrentNormalizedDate();
        if (intent.hasExtra(EXTRA_TEST)) {
            date = 0;
        }

        DateItemLoaderCallbacksWithDate loaderCallbacksWithDate = new DateItemLoaderCallbacksWithDate(context, date, null);
        mLoader = loaderCallbacksWithDate.onCreateLoader(0, null);
        mLoader.registerListener(0, this);
        mLoader.startLoading();

    }

    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            if (data.isNull(data.getColumnIndex(DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL))) {
                pushNotification(mContext);
            }
        } else {
            pushNotification(mContext);
        }
    }

    @Override
    public void onLoadCanceled(Loader<Cursor> loader) {
        if (mLoader != null) {
            mLoader.unregisterListener(this);
            mLoader.cancelLoad();
            mLoader.stopLoading();
        }
    }


}
