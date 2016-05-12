package com.pluviostudios.selfimage.data;

import android.content.Context;
import android.graphics.Bitmap;

import com.pluviostudios.selfimage.dataObjects.MealDiaryObject;

/**
 * Created by Spectre on 5/10/2016.
 */
public class DataManager {

    private Context context;

    public DataManager(Context context) {
        this.context = context;
    }

    public long getLatestDate() {
        return 0;
    }

    public boolean hasDate(long date) {
        return false;
    }

    public int getDateCount() {
        return 0;
    }

    public Bitmap getImage(long date) {
        return null;
    }

    public boolean setImage(long date) {
        return false;
    }

    public MealDiaryObject getMealDiary(long date) {
        return null;
    }

    public boolean setMealDiaryObject(long date) {
        return false;
    }

}
