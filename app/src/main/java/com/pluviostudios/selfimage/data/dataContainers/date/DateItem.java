package com.pluviostudios.selfimage.data.dataContainers.date;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.app.LoaderManager;

import com.pluviostudios.selfimage.data.database.DatabaseContract;

import java.io.Serializable;

/**
 * Created by Spectre on 6/25/2016.
 */
public class DateItem implements Serializable {

    public Long id;
    public long date;
    public String img_dir;

    public DateItem(long date, String img_dir) {
        this.date = date;
        this.img_dir = img_dir;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean hasCurrentImage() {
        return img_dir != null;
    }

    public static void getDateItems(Context context, LoaderManager manager, int loadId, DateItemLoaderCallbacks.OnDateItemsReceived onDateItemsReceived) {
        DateItemLoaderCallbacks callbacks = new DateItemLoaderCallbacks(context, onDateItemsReceived);
        manager.initLoader(loadId, null, callbacks);
    }

    public static void getDateItems(Context context, LoaderManager manager, int loadId, long date, DateItemLoaderCallbacks.OnDateItemsReceived onDateItemsReceived) {
        DateItemLoaderCallbacksWithDate callbacks = new DateItemLoaderCallbacksWithDate(context, date, onDateItemsReceived);
        manager.initLoader(loadId, null, callbacks);
    }

    public void save(Context context) {

        ContentValues contentValues = new ContentValues();

        if (img_dir != null) {
            contentValues.put(DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL, img_dir);
        }

        if (id == null) {
            contentValues.put(DatabaseContract.DateEntry.DATE_COL, date);
            id = DatabaseContract.DateEntry.getIdFromUri(context.getContentResolver().insert(
                    DatabaseContract.DateEntry.CONTENT_URI,
                    contentValues
            ));
        } else {
            context.getContentResolver().update(
                    DatabaseContract.DateEntry.CONTENT_URI,
                    contentValues,
                    null,
                    null
            );

        }

    }

}
