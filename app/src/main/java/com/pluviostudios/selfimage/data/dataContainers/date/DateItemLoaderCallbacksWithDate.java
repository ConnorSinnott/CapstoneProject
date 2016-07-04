package com.pluviostudios.selfimage.data.dataContainers.date;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.pluviostudios.selfimage.data.database.DatabaseContract;

/**
 * Created by Spectre on 6/25/2016.
 */
public class DateItemLoaderCallbacksWithDate extends DateItemLoaderCallbacks {

    long mDate;

    public DateItemLoaderCallbacksWithDate(Context context, long date, OnDateItemsReceived onDateItemsReceived) {
        super(context, null, null, onDateItemsReceived);
        mDate = date;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                mContext,
                DatabaseContract.DateEntry.buildDateWithStartDate(mDate),
                projection,
                selection,
                selectionArgs,
                null);
    }

}
