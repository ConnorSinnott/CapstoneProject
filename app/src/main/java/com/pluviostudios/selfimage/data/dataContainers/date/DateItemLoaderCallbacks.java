package com.pluviostudios.selfimage.data.dataContainers.date;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.pluviostudios.selfimage.data.database.DatabaseContract;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/25/2016.
 */
public class DateItemLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

    protected Context mContext;
    protected OnDateItemsReceived mOnDateItemsReceived;
    protected String selection;
    protected String[] selectionArgs;

    public DateItemLoaderCallbacks(Context context, String selection, String[] selectionArgs, OnDateItemsReceived onDateItemsReceived) {
        mContext = context;
        mOnDateItemsReceived = onDateItemsReceived;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
    }

    protected final String[] projection = new String[]{
            DatabaseContract.DateEntry._ID,
            DatabaseContract.DateEntry.DATE_COL,
            DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                DatabaseContract.DateEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        ArrayList<DateItem> list = new ArrayList<>();
        if (data.moveToFirst()) {
            do {

                long id = data.getLong(0);

                long date = data.getLong(1);
                String img_dir = null;
                if (!data.isNull(2)) {
                    img_dir = data.getString(2);
                }

                DateItem item = new DateItem(date, img_dir);
                item.setId(id);
                list.add(item);

            } while (data.moveToNext());
        }
        mOnDateItemsReceived.onDateItemsReceived(list);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface OnDateItemsReceived {
        void onDateItemsReceived(ArrayList<DateItem> dateItems);
    }

}
