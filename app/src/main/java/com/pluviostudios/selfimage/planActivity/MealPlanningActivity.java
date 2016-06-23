package com.pluviostudios.selfimage.planActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.planActivity.fragments.planning.MealPlanFragment;
import com.pluviostudios.selfimage.planActivity.fragments.search.FoodSearchFragment;
import com.pluviostudios.selfimage.utilities.MissingExtraException;

/**
 * Created by Spectre on 5/10/2016.
 */
public class MealPlanningActivity extends AppCompatActivity {

    public static final String REFERENCE_ID = "MealPlanningActivity";

    public static final String EXTRA_DATE = "extra_date";

    public static Intent buildMealPlanActivityIntent(Context context, long date) {
        Intent intent = new Intent(context, MealPlanningActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_planning_activity);

        if (!getIntent().hasExtra(EXTRA_DATE)) {
            throw new MissingExtraException(EXTRA_DATE);
        }

        long date = getIntent().getExtras().getLong(EXTRA_DATE);
        getIntent().getExtras().clear();

        Log.i(REFERENCE_ID, String.valueOf(date));

        MealPlanFragment fragment = MealPlanFragment.buildMealPlanFragment(this, date);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.meal_planning_activity_main_frame, fragment, FoodSearchFragment.REFERENCE_TAG);
        ft.commit();

    }

    public void showFoodSearchFragment() {

        FoodSearchFragment fragment = FoodSearchFragment.buildFoodSearchFragment(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.meal_planning_activity_main_frame, fragment, FoodSearchFragment.REFERENCE_TAG);
        ft.addToBackStack("Stack");
        ft.commit();

    }

}
