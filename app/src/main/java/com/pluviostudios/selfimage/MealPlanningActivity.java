package com.pluviostudios.selfimage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

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
    }


}
