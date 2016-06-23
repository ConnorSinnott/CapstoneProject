package com.pluviostudios.selfimage.data.dataContainers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.pluviostudios.selfimage.data.database.DatabaseContract;
import com.pluviostudios.selfimage.data.dataContainers.FoodItemWithDB;
import com.pluviostudios.selfimage.utilities.Utilities;
import com.pluviostudios.usdanutritionalapi.FoodItem;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/19/2016.
 */
public class FoodItemDBHandler {

    public static void insertIntoMealPlan(Context context, long date, FoodItemWithDB foodItem, int quantity, int category) {

        if (!foodItem.hasNutrientData()) {
            throw new RuntimeException("Food item is missing nutrient data");
        }

        if (!hasFoodInDatabase(context, foodItem)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.FoodEntry.ITEM_NAME_COL, foodItem.getFoodName());
            contentValues.put(DatabaseContract.FoodEntry.ITEM_NDBNO_COL, foodItem.getFoodNDBNO());
            contentValues.put(DatabaseContract.FoodEntry.ITEM_LAST_ACCESSED, date);
            contentValues.put(DatabaseContract.FoodEntry.ITEM_CALORIE_COL, foodItem.getNutrientData().get(FoodItem.Calories));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_PROTEIN_COL, foodItem.getNutrientData().get(FoodItem.Protein));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_FAT_COL, foodItem.getNutrientData().get(FoodItem.Fat));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_CARBS_COL, foodItem.getNutrientData().get(FoodItem.Carbs));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_FIBER_COL, foodItem.getNutrientData().get(FoodItem.Fiber));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_SATFAT_COL, foodItem.getNutrientData().get(FoodItem.SatFat));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_MONOFAT_COL, foodItem.getNutrientData().get(FoodItem.MonoFat));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_POLYFAT_COL, foodItem.getNutrientData().get(FoodItem.PolyFat));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_CHOLESTEROL_COL, foodItem.getNutrientData().get(FoodItem.Cholesterol));
            context.getContentResolver().insert(DatabaseContract.FoodEntry.CONTENT_URI, contentValues);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.FoodEntry.ITEM_LAST_ACCESSED, date);
            context.getContentResolver().update(
                    DatabaseContract.FoodEntry.buildFoodWithNDBNO(foodItem.getFoodNDBNO()),
                    contentValues,
                    null,
                    null
            );
        }

        int currQuantity = getFoodQuantity(context, foodItem, category);
        if (currQuantity < 1) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_DATE_COL, date);
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_NDBNO_COL, foodItem.getFoodNDBNO());
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL, category);
            contentValues.put(DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL, quantity);
            context.getContentResolver().insert(DatabaseContract.DiaryEntry.CONTENT_URI, contentValues);
        } else {
            updateDiaryItem(context, date, foodItem, category, quantity + currQuantity, category);
        }

    }

    public static void updateDiaryItem(Context context, long date, FoodItemWithDB foodItem, int category, int newQuantity, int newCategory) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL, newCategory);
        contentValues.put(DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL, newQuantity);
        context.getContentResolver().update(
                DatabaseContract.DiaryEntry.buildDiaryWithStartDateAndCategoryAndNDBNO(
                        date,
                        foodItem.getFoodNDBNO(),
                        category
                ),
                contentValues,
                null,
                null
        );

    }

    public static int removeFromMealPlan(Context context, long date, FoodItemWithDB foodItem, int category) {

        int rowsDeleted = context.getContentResolver().delete(
                DatabaseContract.DiaryEntry.buildDiaryWithStartDateAndCategoryAndNDBNO(
                        date,
                        foodItem.getFoodNDBNO(),
                        category
                ), null, null
        );

        return rowsDeleted;

    }

    public static boolean hasFoodInDatabase(Context context, FoodItemWithDB foodItem) {

        boolean out = false;

        Cursor c = context.getContentResolver().query(
                DatabaseContract.FoodEntry.buildFoodWithNDBNO(foodItem.getFoodNDBNO()), null, null, null, null
        );

        if (c != null && c.moveToFirst()) {
            out = true;
            c.close();
        }

        return out;

    }

    public static void pullNutritionalArrayFromDatabase(Context context, FoodItemWithDB foodItem, FoodItemWithDB.OnDataPulledWithDB onDataPulledWithDB) {

        ArrayList<Double> nutritionalData = null;

        final String[] projection = new String[]{
                DatabaseContract.FoodEntry.ITEM_CALORIE_COL,
                DatabaseContract.FoodEntry.ITEM_PROTEIN_COL,
                DatabaseContract.FoodEntry.ITEM_FAT_COL,
                DatabaseContract.FoodEntry.ITEM_CARBS_COL,
                DatabaseContract.FoodEntry.ITEM_FIBER_COL,
                DatabaseContract.FoodEntry.ITEM_SATFAT_COL,
                DatabaseContract.FoodEntry.ITEM_MONOFAT_COL,
                DatabaseContract.FoodEntry.ITEM_POLYFAT_COL,
                DatabaseContract.FoodEntry.ITEM_CHOLESTEROL_COL,
        };

        Cursor c = context.getContentResolver().query(
                DatabaseContract.FoodEntry.buildFoodWithNDBNO(foodItem.getFoodNDBNO()),
                projection,
                null,
                null,
                null
        );

        if (c != null && c.moveToFirst()) {

            nutritionalData = new ArrayList<>();
            for (int i = 0; i < projection.length; i++) {
                nutritionalData.add(c.getDouble(i));
            }
            c.close();

            foodItem.putNutrientData(nutritionalData);

            if (onDataPulledWithDB != null) {
                onDataPulledWithDB.onDataPulled(foodItem);
            }

        }

    }

    public static int getFoodQuantity(Context context, FoodItemWithDB foodItem, int category) {

        int itemQuantity = 0;

        Cursor c = context.getContentResolver().query(
                DatabaseContract.DiaryEntry.buildDiaryWithStartDateAndCategoryAndNDBNO(
                        Utilities.getCurrentNormalizedDate(),
                        foodItem.getFoodNDBNO(),
                        category
                ), null, null, null, null
        );

        if (c != null && c.moveToFirst()) {
            itemQuantity = c.getInt(c.getColumnIndex(DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL));
            c.close();
        }

        return itemQuantity;

    }

}
