package com.pluviostudios.selfimage.data.dataContainers;

import android.content.Context;
import android.database.Cursor;

import com.pluviostudios.selfimage.data.database.DatabaseContract;

import java.io.Serializable;

/**
 * Created by Spectre on 6/22/2016.
 */
public class DiaryItem implements Serializable {

    public long date;
    public int quantity;
    public int category;
    public FoodItemWithDB foodItem;

    public DiaryItem(FoodItemWithDB foodItem, long date, int category, int quantity) {
        this.quantity = quantity;
        this.date = date;
        this.category = category;
        this.foodItem = foodItem;
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

    public void save() {

        // TODO Setup Save!!!

    }

}
