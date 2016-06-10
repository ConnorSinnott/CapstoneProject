package com.pluviostudios.selfimage.planActivity.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v4.app.Fragment;

import com.pluviostudios.selfimage.data.DatabaseContract;
import com.pluviostudios.selfimage.planActivity.data.FoodItem;
import com.pluviostudios.selfimage.utilities.Utilities;

/**
 * Created by Spectre on 6/7/2016.
 */
public class BaseMealPlanningFragment extends Fragment {

    private MealPlanningActivity mMealPlanningActivity;

    public void setMealPlanningActivity(MealPlanningActivity mealPlanningActivity) {
        mMealPlanningActivity = mealPlanningActivity;
    }

    protected void showFoodSearchFragment() {
        mMealPlanningActivity.showFoodSearchFragment();
    }

    protected void addToMealPlan(FoodItem foodItem, int quantity, int category) {

        ContentValues contentValues = new ContentValues();

        int currQuantity = hasFoodItem(foodItem, category);

        if (currQuantity < 1) {
            contentValues.put(DatabaseContract.DiaryEntry.DATE_COL, Utilities.getCurrentNormalizedDate());
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_NAME_COL, foodItem.getName());
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_NDBNO_COL, foodItem.getNdbmo());
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL, category);
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL, quantity);
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_CALORIE_COL, foodItem.getValue(FoodItem.Calories));
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_PROTEIN_COL, foodItem.getValue(FoodItem.Protein));
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_FAT_COL, foodItem.getValue(FoodItem.Fat));
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_CARBS_COL, foodItem.getValue(FoodItem.Carbs));
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_FIBER_COL, foodItem.getValue(FoodItem.Fiber));
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_SATFAT_COL, foodItem.getValue(FoodItem.SatFat));
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_MONOFAT_COL, foodItem.getValue(FoodItem.MonoFat));
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_POLYFAT_COL, foodItem.getValue(FoodItem.PolyFat));
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_CHOLESTEROL_COL, foodItem.getValue(FoodItem.Cholesterol));
            mMealPlanningActivity.getContentResolver().insert(DatabaseContract.DiaryEntry.CONTENT_URI, contentValues);
        } else {
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL, quantity + currQuantity);
            mMealPlanningActivity.getContentResolver().update(
                    DatabaseContract.DiaryEntry.buildDiaryWithStartDateAndCategoryAndNDBNO(
                            Utilities.getCurrentNormalizedDate(),
                            foodItem.getNdbmo(),
                            category
                    ), contentValues, null, null
            );
        }

    }

    protected int removeFromMealPlan(FoodItem foodItem, int quantity, int category) {

        int rowsDeleted = mMealPlanningActivity.getContentResolver().delete(
                DatabaseContract.DiaryEntry.buildDiaryWithStartDateAndCategoryAndNDBNO(
                        Utilities.getCurrentNormalizedDate(),
                        foodItem.getNdbmo(),
                        category
                ), null, null
        );

        return rowsDeleted;

    }

    protected int hasFoodItem(FoodItem foodItem, int category) {

        int foodItemQuantity = 0;

        Cursor c = mMealPlanningActivity.getContentResolver().query(
                DatabaseContract.DiaryEntry.buildDiaryWithStartDateAndCategoryAndNDBNO(
                        Utilities.getCurrentNormalizedDate(),
                        foodItem.getNdbmo(),
                        category
                ), null, null, null, null
        );

        if (c != null && c.moveToFirst()) {
            foodItemQuantity = c.getInt(0);
            c.close();
        }

        return foodItemQuantity;

    }

}
