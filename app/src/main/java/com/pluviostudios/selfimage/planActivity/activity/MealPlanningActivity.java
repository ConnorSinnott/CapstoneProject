package com.pluviostudios.selfimage.planActivity.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.planActivity.planning.MealPlanFragment;
import com.pluviostudios.selfimage.planActivity.search.FoodSearchFragment;

/**
 * Created by Spectre on 5/10/2016.
 */
public class MealPlanningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_planning_activity);

        MealPlanFragment fragment = new MealPlanFragment();
        fragment.setMealPlanningActivity(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.meal_planning_activity_main_frame, fragment, FoodSearchFragment.REFERENCE_TAG);
        ft.commit();

    }

    public void showFoodSearchFragment() {

        FoodSearchFragment fragment = new FoodSearchFragment();
        fragment.setMealPlanningActivity(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.meal_planning_activity_main_frame, fragment, FoodSearchFragment.REFERENCE_TAG);
        ft.addToBackStack("Stack");
        ft.commit();

    }

}
