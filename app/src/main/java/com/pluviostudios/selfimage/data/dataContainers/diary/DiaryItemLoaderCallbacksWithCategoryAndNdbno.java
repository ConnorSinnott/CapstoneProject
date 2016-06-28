package com.pluviostudios.selfimage.data.dataContainers.diary;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.pluviostudios.selfimage.data.database.DatabaseContract;

/**
 * Created by Spectre on 6/25/2016.
 */
public class DiaryItemLoaderCallbacksWithCategoryAndNdbno extends DiaryItemLoaderCallbacksWithCategory {

    protected String mNDBNO;

    public DiaryItemLoaderCallbacksWithCategoryAndNdbno(Context context, long date, int category, String ndbno, OnDiaryItemsReceived onDiaryItemsReceived) {
        super(context, date, category, onDiaryItemsReceived);
        mNDBNO = ndbno;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                DatabaseContract.DiaryEntry.buildDiaryWithStartDateAndCategoryAndNDBNO(mDate, mNDBNO, mCategory),
                projection,
                null,
                null,
                sortOrder
        );
    }

}
