package com.pluviostudios.selfimage.planActivity.planning;

import com.pluviostudios.selfimage.planActivity.data.FoodItem;

/**
 * Created by Spectre on 6/9/2016.
 */
public class MealPlanFragmentRecyclerItem {

    private FoodItem mFoodItem;
    private String mLabel;

    public boolean isLabel() {
        return mLabel != null;
    }

    public MealPlanFragmentRecyclerItem(FoodItem foodItem) {
        mFoodItem = foodItem;
    }

    public MealPlanFragmentRecyclerItem(String label) {
        mLabel = label;
    }

    public FoodItem getFoodItem() {
        return mFoodItem;
    }

    public String getLabel() {
        return mLabel;
    }

}
