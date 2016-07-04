package com.pluviostudios.selfimage.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.database.DatabaseContract;
import com.pluviostudios.selfimage.mainActivity.main.MainActivity;
import com.pluviostudios.selfimage.utilities.DateUtils;
import com.pluviostudios.selfimage.views.CalorieBar;

/**
 * Created by spectre on 7/3/16.
 */
public class CalBarProvider extends AppWidgetProvider {

    public static final String REFERENCE_ID = "CalBarProvider";

    public static void updateWidget(Context context) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName componentWidget = new ComponentName(context, CalBarProvider.class);

        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(componentWidget);

        CalBarProvider calBarProvider = new CalBarProvider();
        calBarProvider.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int x : appWidgetIds) {
            updateAppById(context, appWidgetManager, x);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppById(context, appWidgetManager, appWidgetId);
    }

    private static void updateAppById(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);

        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        RemoteViews views = buildRemoteView(context, width, height);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    private static int getCalories(Context context) {

        final String[] projection = {DatabaseContract.FoodEntry.ITEM_CALORIE_COL, DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL};
        final String selection = DatabaseContract.FoodEntry.ITEM_CALORIE_COL + " > ? ";
        final String[] selectionArgs = {"0"};

        Cursor c = context.getContentResolver().query(
                DatabaseContract.DiaryEntry.buildDiaryWithStartDate(DateUtils.getCurrentNormalizedDate()),
                projection,
                selection,
                selectionArgs,
                null,
                null
        );

        int count = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    count += (int) c.getDouble(0) * c.getInt(1);
                } while (c.moveToNext());
            }
            c.close();
        }

        return count;

    }

    private static RemoteViews buildRemoteView(Context context, int width, int height) {

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.calbar_widget_layout);

        Bitmap bitmap = getCalorieBarBitmap(context, width, height);
        views.setImageViewBitmap(R.id.calbar_widget_layout_imageview, bitmap);
        views.setOnClickPendingIntent(R.id.calbar_widget_layout_imageview, pendingIntent);

        return views;

    }

    private static Bitmap getCalorieBarBitmap(Context context, int width, int height) {

        CalorieBar myView = new CalorieBar(context);
        myView.setProgress(getCalories(context));

        int newWidth = getWidthSpec(context, width);
        int newHeight = getHeightSpec(context, height);

        myView.measure(newWidth, newHeight);
        myView.layout(0, 0, newWidth, newHeight);

        myView.setDrawingCacheEnabled(true);
        return myView.getDrawingCache();

    }

    private static int getWidthSpec(Context context, int width) {

        Resources r = context.getResources();
        int newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, r.getDisplayMetrics());
        int widthPixels = View.MeasureSpec.getSize(newWidth);
        int widthMode = View.MeasureSpec.getMode(View.MeasureSpec.UNSPECIFIED);
        return View.MeasureSpec.makeMeasureSpec(widthPixels, widthMode);

    }

    private static int getHeightSpec(Context context, int height) {

        Resources r = context.getResources();
        int newHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, r.getDisplayMetrics());
        int heightPixels = View.MeasureSpec.getSize(newHeight);
        int heightMode = View.MeasureSpec.getMode(View.MeasureSpec.UNSPECIFIED);
        return View.MeasureSpec.makeMeasureSpec(heightPixels, heightMode);

    }


}
