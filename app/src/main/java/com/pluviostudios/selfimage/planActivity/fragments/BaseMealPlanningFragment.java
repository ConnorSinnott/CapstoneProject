package com.pluviostudios.selfimage.planActivity.fragments;

import android.support.v4.app.Fragment;

import com.pluviostudios.selfimage.planActivity.MealPlanningActivity;

/**
 * Created by Spectre on 6/19/2016.
 */
public class BaseMealPlanningFragment extends Fragment {

    MealPlanningActivity mMealPlanningActivity;

    public void setMealPlanningActivity(MealPlanningActivity mealPlanningActivity) {
        this.mMealPlanningActivity = mealPlanningActivity;
    }

    public void showFoodSearchFragment() {
        mMealPlanningActivity.showFoodSearchFragment();
    }

}
