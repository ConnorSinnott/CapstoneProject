package com.pluviostudios.selfimage.data.dataContainers.food;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.database.DatabaseContract;
import com.pluviostudios.selfimage.utilities.DateUtils;
import com.pluviostudios.usdanutritionalapi.AsyncFoodItemSearch;
import com.pluviostudios.usdanutritionalapi.FoodItem;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/22/2016.
 */
public class FoodItemWithDB extends FoodItem {

    private static AsyncFoodItemSearch sAsyncFoodItemSearch = null;

    public FoodItemWithDB(String foodName, String foodNDBNO) {
        super(foodName, foodNDBNO);
    }

    public FoodItemWithDB(FoodItem foodItem) {
        super(foodItem.foodName, foodItem.foodNDBNO);
        if (foodItem.hasNutrientData()) {
            setNutrientData(foodItem.getNutrientData());
        }
    }

    public void save(Context context) {

        if (!hasNutrientData()) {
            getNutrientDataWithDB(context, new OnDataPulledWithDB() {

                private Context mContext;

                public OnDataPulledWithDB setContext(Context context) {
                    mContext = context;
                    return this;
                }

                @Override
                public void onDataPulled(FoodItemWithDB foodItemWithDB) {
                    if (foodItemWithDB.hasNutrientData()) {
                        save(mContext);
                    }
                }

            }.setContext(context));
            return;
        }

        if (!hasFoodInDatabase(context, this)) {
            // Add food data into the food table
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.FoodEntry.ITEM_NAME_COL, foodName);
            contentValues.put(DatabaseContract.FoodEntry.ITEM_NDBNO_COL, foodNDBNO);
            contentValues.put(DatabaseContract.FoodEntry.ITEM_LAST_ACCESSED, DateUtils.getCurrentNormalizedDate());
            contentValues.put(DatabaseContract.FoodEntry.ITEM_CALORIE_COL, getNutrientData().get(FoodItem.Calories));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_PROTEIN_COL, getNutrientData().get(FoodItem.Protein));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_FAT_COL, getNutrientData().get(FoodItem.Fat));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_CARBS_COL, getNutrientData().get(FoodItem.Carbs));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_FIBER_COL, getNutrientData().get(FoodItem.Fiber));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_SATFAT_COL, getNutrientData().get(FoodItem.SatFat));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_MONOFAT_COL, getNutrientData().get(FoodItem.MonoFat));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_POLYFAT_COL, getNutrientData().get(FoodItem.PolyFat));
            contentValues.put(DatabaseContract.FoodEntry.ITEM_CHOLESTEROL_COL, getNutrientData().get(FoodItem.Cholesterol));
            context.getContentResolver().insert(DatabaseContract.FoodEntry.CONTENT_URI, contentValues);
        } else {
            // Update Last Accessed. In the future this can be used remove unused food data
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.FoodEntry.ITEM_LAST_ACCESSED, DateUtils.getCurrentNormalizedDate());
            context.getContentResolver().update(
                    DatabaseContract.FoodEntry.buildFoodWithNDBNO(foodNDBNO),
                    contentValues,
                    null,
                    null
            );
        }
    }

    public void getNutrientDataWithDB(Context context, OnDataPulledWithDB onDataPulledWithDB) {

        // getNutrientDataWithDB checks to see if its nutrient data is stored locally, otherwise it will fetch from online

        if (hasNutrientData()) {
            onDataPulledWithDB.onDataPulled(this);
        } else {
            if (hasFoodInDatabase(context, this)) {
                pullNutritionalArrayFromDatabase(context, this, onDataPulledWithDB);
            } else {
                super.pullNutrientData(context.getString(R.string.usda_api), new FoodItem.OnDataPulled() {
                    OnDataPulledWithDB reRoute;

                    public FoodItem.OnDataPulled setReRoute(OnDataPulledWithDB reRoute) {
                        this.reRoute = reRoute;
                        return this;
                    }

                    @Override
                    public void OnDataPulled(FoodItem foodItem) {
                        reRoute.onDataPulled(new FoodItemWithDB(foodItem));
                    }

                }.setReRoute(onDataPulledWithDB));
            }
        }
    }

    public static void pullNutritionalArrayFromDatabase(Context context, FoodItemWithDB foodItem, FoodItemWithDB.OnDataPulledWithDB onDataPulledWithDB) {

        ArrayList<Double> nutritionalData;

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
                DatabaseContract.FoodEntry.buildFoodWithNDBNO(foodItem.foodNDBNO),
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

            foodItem.setNutrientData(nutritionalData);

            if (onDataPulledWithDB != null) {
                onDataPulledWithDB.onDataPulled(foodItem);
            }

        }

    }

    public static boolean hasFoodInDatabase(Context context, FoodItemWithDB foodItem) {
        boolean out = false;
        Cursor c = context.getContentResolver().query(
                DatabaseContract.FoodEntry.buildFoodWithNDBNO(foodItem.foodNDBNO), null, null, null, null
        );
        if (c != null && c.moveToFirst()) {
            out = true;
            c.close();
        }
        return out;
    }

    public static void getFoodItems(Context context, AsyncFoodItemSearch.OnAsyncNDBNOSearchResult onResult, String searchString) {

        // This method will generate a list of FoodItems from the USDA database

        if (sAsyncFoodItemSearch != null && !sAsyncFoodItemSearch.isCancelled())
            sAsyncFoodItemSearch.cancel(true);

        String usdaAPI = context.getString(R.string.usda_api);
        sAsyncFoodItemSearch = new AsyncFoodItemSearch(usdaAPI, onResult);
        sAsyncFoodItemSearch.execute(searchString);

    }

    public interface OnDataPulledWithDB {
        void onDataPulled(FoodItemWithDB foodItemWithDB);
    }

}
