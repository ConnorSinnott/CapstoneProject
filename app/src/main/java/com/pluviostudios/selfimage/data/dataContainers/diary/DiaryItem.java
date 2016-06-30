package com.pluviostudios.selfimage.data.dataContainers.diary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

import com.pluviostudios.selfimage.data.dataContainers.food.FoodItemWithDB;
import com.pluviostudios.selfimage.data.database.DatabaseContract;

import java.io.Serializable;

/**
 * Created by Spectre on 6/22/2016.
 */
public class DiaryItem implements Serializable {

    public static final String REFERENCE_ID = "DiaryItem";

    public Long id;
    public long date;
    public int quantity;
    public int category;
    public FoodItemWithDB foodItem;

    public DiaryItem(FoodItemWithDB foodItem, long date, int category, int quantity) {
        this.date = date;
        this.category = category;
        this.quantity = quantity;
        this.foodItem = foodItem;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static void getDiaryItems(Context context, LoaderManager loaderManager, int loadId, long date, DiaryItemLoaderCallbacks.OnDiaryItemsReceived onDiaryItemsReceived) {
        DiaryItemLoaderCallbacks callbacks = new DiaryItemLoaderCallbacks(context, date, onDiaryItemsReceived);
        loaderManager.initLoader(loadId, null, callbacks);
    }

    public static void getNutrientTotals(Context context, LoaderManager loaderManager, int loadId, long date, DiaryItemNutrientTotals.OnNutrientTotalsReceived onNutrientTotalsReceived) {
        DiaryItemNutrientTotals callback = new DiaryItemNutrientTotals(context, date, onNutrientTotalsReceived);
        loaderManager.initLoader(loadId, null, callback);
    }

    public String getCategoryName(Context context) {
        String out = null;
        Cursor c = context.getContentResolver().query(
                DatabaseContract.CategoryEntry.buildCategoryWithIndex(category),
                new String[]{DatabaseContract.CategoryEntry.CATEGORY_NAME_COL},
                null,
                null,
                null);
        if (c != null && c.moveToFirst()) {
            out = c.getString(0);
            c.close();
        }
        return out;
    }

    public void save(Context context) {

        if (id != null || mergeWithExisting(context)) {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL, category);
            values.put(DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL, quantity);
            context.getContentResolver().update(
                    DatabaseContract.DiaryEntry.CONTENT_URI,
                    values,
                    DatabaseContract.DiaryEntry._ID + " = ?",
                    new String[]{
                            String.valueOf(id)
                    }
            );
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_DATE_COL, date);
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_NDBNO_COL, foodItem.getFoodNDBNO());
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL, category);
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL, quantity);
            id = DatabaseContract.DiaryEntry.getIdFromUri(
                    context.getContentResolver().insert(DatabaseContract.DiaryEntry.CONTENT_URI, contentValues));
        }

        foodItem.save(context);

    }

    private boolean mergeWithExisting(Context context) {

        Cursor c = context.getContentResolver().query(
                DatabaseContract.DiaryEntry.buildDiaryWithStartDateAndCategoryAndNDBNO(date, foodItem.getFoodNDBNO(), category),
                new String[]{
                        DatabaseContract.DiaryEntry.TABLE_NAME + "." + DatabaseContract.DiaryEntry._ID,
                        DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL},
                null,
                null,
                null,
                null
        );

        if (c != null) {
            if (c.moveToFirst()) {
                id = c.getLong(0);
                quantity += c.getInt(1);
                c.close();
                return true;
            }
            c.close();
        }
        return false;

    }

    public void delete(Context context) {

        if (id == null)
            return;

        context.getContentResolver().delete(
                DatabaseContract.DiaryEntry.CONTENT_URI,
                DatabaseContract.DiaryEntry._ID + " = ?",
                new String[]{
                        String.valueOf(id)
                }
        );

    }


}
