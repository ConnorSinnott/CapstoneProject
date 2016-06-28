package com.pluviostudios.selfimage.data.dataContainers.diary;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.pluviostudios.selfimage.data.database.DatabaseContract;
import com.pluviostudios.selfimage.utilities.DateUtils;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/25/2016.
 */
public class DiaryItemNutrientTotals implements LoaderManager.LoaderCallbacks<Cursor> {

    protected Context mContext;
    protected long mDate;
    protected long mNutrientId;
    protected OnNutrientTotalsReceived mOnNutrientTotalsReceived;

    private final String[] projection = {
            DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL,
            DatabaseContract.FoodEntry.ITEM_CALORIE_COL,
            DatabaseContract.FoodEntry.ITEM_PROTEIN_COL,
            DatabaseContract.FoodEntry.ITEM_FAT_COL,
            DatabaseContract.FoodEntry.ITEM_CARBS_COL,
            DatabaseContract.FoodEntry.ITEM_FIBER_COL,
            DatabaseContract.FoodEntry.ITEM_SATFAT_COL,
            DatabaseContract.FoodEntry.ITEM_MONOFAT_COL,
            DatabaseContract.FoodEntry.ITEM_POLYFAT_COL,
            DatabaseContract.FoodEntry.ITEM_CHOLESTEROL_COL
    };

    public DiaryItemNutrientTotals(Context context, long date, OnNutrientTotalsReceived onNutrientTotalsReceived) {
        mContext = context;
        mDate = date;
        mOnNutrientTotalsReceived = onNutrientTotalsReceived;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                DatabaseContract.DiaryEntry.buildDiaryWithStartDate(DateUtils.getCurrentNormalizedDate()),
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Double> out = new ArrayList<>();

        for (int i = 0; i < projection.length; i++) {
            out.add(0.0);
        }

        if (data.moveToFirst()) {
            do {
                for (int i = 1; i < projection.length; i++) {
                    double curr = out.get(i - 1);
                    out.set(i - 1, curr + (data.getDouble(i) * data.getInt(0)));
                }
            } while (data.moveToNext());
        }
        mOnNutrientTotalsReceived.onNutrientTotalsReceived(out);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface OnNutrientTotalsReceived {
        void onNutrientTotalsReceived(ArrayList<Double> totals);
    }

}
