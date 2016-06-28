package com.pluviostudios.selfimage.data.dataContainers.diary;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.pluviostudios.selfimage.data.dataContainers.food.FoodItemWithDB;
import com.pluviostudios.selfimage.data.database.DatabaseContract;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/25/2016.
 */
public class DiaryItemLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

    protected Context mContext;
    protected long mDate;
    protected OnDiaryItemsReceived mOnDiaryItemsReceived;

    public DiaryItemLoaderCallbacks(Context context, long date, OnDiaryItemsReceived onDiaryItemsReceived) {
        mDate = date;
        mOnDiaryItemsReceived = onDiaryItemsReceived;
        mContext = context;
    }

    protected final String[] projection = new String[]{
            DatabaseContract.DiaryEntry.TABLE_NAME + "." + DatabaseContract.DiaryEntry._ID,
            DatabaseContract.FoodEntry.ITEM_NAME_COL,
            DatabaseContract.DiaryEntry.ITEM_NDBNO_COL,
            DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL,
            DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL,

    };
    protected final String sortOrder = DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL + " ASC ";

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                DatabaseContract.DiaryEntry.buildDiaryWithStartDate(mDate),
                projection,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<DiaryItem> list = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                int id = data.getInt(0);
                int itemQuantity = data.getInt(3);
                int itemCategory = data.getInt(4);
                String foodName = data.getString(1);
                String foodNDBNO = data.getString(2);

                FoodItemWithDB foodItemWithDB = new FoodItemWithDB(foodName, foodNDBNO);
                DiaryItem diaryItem = new DiaryItem(foodItemWithDB, mDate, itemCategory, itemQuantity);
                diaryItem.setId(id);

                list.add(diaryItem);
            } while (data.moveToNext());
        }
        mOnDiaryItemsReceived.onDiaryItemsReceived(list);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public interface OnDiaryItemsReceived {
        void onDiaryItemsReceived(ArrayList<DiaryItem> diaryItems);
    }

}
