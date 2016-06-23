package com.pluviostudios.selfimage.data.dataContainers;

import android.content.Context;

import com.pluviostudios.usdanutritionalapi.FoodItem;

/**
 * Created by Spectre on 6/22/2016.
 */
public class FoodItemWithDB extends FoodItem {

    public FoodItemWithDB(String foodName, String foodNDBNO) {
        super(foodName, foodNDBNO);
    }

    public FoodItemWithDB(FoodItem foodItem) {
        super(foodItem.getFoodName(), foodItem.getFoodNDBNO());
        if (foodItem.hasNutrientData()) {
            putNutrientData(foodItem.getNutrientData());
        }
    }

    public void pullNutrientDataWithDB(Context context, OnDataPulledWithDB onDataPulledWithDB) {
        if (FoodItemDBHandler.hasFoodInDatabase(context, this)) {
            FoodItemDBHandler.pullNutritionalArrayFromDatabase(context, this, onDataPulledWithDB);
        } else {
            super.pullNutrientData(context, new FoodItem.OnDataPulled() {
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

    public interface OnDataPulledWithDB {
        void onDataPulled(FoodItemWithDB foodItemWithDB);
    }


}
