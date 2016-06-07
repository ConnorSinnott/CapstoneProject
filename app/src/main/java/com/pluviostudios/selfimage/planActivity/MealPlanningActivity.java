package com.pluviostudios.selfimage.planActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.pluviostudios.selfimage.R;

/**
 * Created by Spectre on 5/10/2016.
 */
public class MealPlanningActivity extends AppCompatActivity {

    private FrameLayout mMainFrame;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_planning_activity);

        mMainFrame = (FrameLayout) findViewById(R.id.meal_planning_activity_main_frame);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.meal_planning_activity_main_frame, new FoodSearchFragment(), FoodSearchFragment.REFERENCE_TAG);
        ft.commit();

    }

}
