package com.pluviostudios.selfimage.mainActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.dataContainers.date.DateItem;
import com.pluviostudios.selfimage.data.dataContainers.date.DateItemLoaderCallbacks;
import com.pluviostudios.selfimage.data.dataContainers.diary.DiaryItem;
import com.pluviostudios.selfimage.data.dataContainers.diary.DiaryItemNutrientTotals;
import com.pluviostudios.selfimage.data.dataContainers.food.FoodItemWithDB;
import com.pluviostudios.selfimage.mainActivity.planning.MealPlanFragment;
import com.pluviostudios.selfimage.notification.DailyNotification;
import com.pluviostudios.selfimage.utilities.DateUtils;
import com.pluviostudios.selfimage.views.CalorieBar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.viewpagerindicator.LinePageIndicator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String REFERENCE_ID = "MainActivity";

    private DateItem mCurrentDateItem;

    private CameraHandler mCameraHandler;
    private TextView mPromptBar;
    private ViewPager mViewPager;
    private LinePageIndicator mTabPageIndicator;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private CalorieBar mCalBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        mCameraHandler = new CameraHandler(MainActivity.this);

        DayCardFragment.setOnFABClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraHandler.takeCameraImage(MainActivity.this);
            }
        });

        DateItem.getDateItems(this, getSupportLoaderManager(), 0, new DateItemLoaderCallbacks.OnDateItemsReceived() {
            @Override
            public void onDateItemsRecieved(ArrayList<DateItem> dateItems) {
                setDayData(dateItems);
            }
        });

        DiaryItem.getNutrientTotals(this, getSupportLoaderManager(), 1, DateUtils.getCurrentNormalizedDate(), new DiaryItemNutrientTotals.OnNutrientTotalsReceived() {
            @Override
            public void onNutrientTotalsReceived(ArrayList<Double> totals) {
                mCalBar.setProgress((int) Math.round(totals.get(FoodItemWithDB.Calories)));
            }
        });

        getSupportFragmentManager().beginTransaction().replace(
                R.id.activity_main_meal_plan_frame,
                MealPlanFragment.buildMealPlanFragment(DateUtils.getCurrentNormalizedDate())
        ).commit();

    }

    private void init() {
        mPromptBar = (TextView) findViewById(R.id.activity_main_prompt_bar);
        mViewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        mTabPageIndicator = (LinePageIndicator) findViewById(R.id.activity_main_tab_page_indicator);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.activity_main_sliding_up_panel);
        mCalBar = (CalorieBar) findViewById(R.id.activity_main_calorie_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options_settings:
//                Intent intent = new Intent(this, SettingsActivity.class);
//                startActivity(intent);
                DailyNotification.pushNotification(MainActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void displayPromptMessage(String message, int color) {
        mPromptBar.setText(message);
        mPromptBar.setBackgroundColor(color);
    }

    protected void setDayData(ArrayList<DateItem> dateItems) {

        if (dateItems.size() == 0) {
            DateItem newDateItem = new DateItem(DateUtils.getCurrentNormalizedDate(), null);
            newDateItem.save(getApplicationContext());
            return;
        }

        mCurrentDateItem = dateItems.get(dateItems.size() - 1);

        displayPromptMessage(
                mCurrentDateItem.hasCurrentImage() ?
                        getString(R.string.has_current_image) :
                        getString(R.string.no_current_image),
                mCurrentDateItem.hasCurrentImage() ?
                        Color.GREEN :
                        Color.RED
        );

        // Implement the ImagePagerAdapter
        mViewPager.setAdapter(new DayCardPagerAdapter(getSupportFragmentManager(), dateItems));

        // Setup the ImagePagerAdapter's Indicator
        mTabPageIndicator.setViewPager(mViewPager);

        // Set the current page to the most recent image;
        mViewPager.setCurrentItem(dateItems.size() - 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            // CameraHandler will request permissions if they are required. Try again upon result.
            case CameraHandler.CAMERA_ACTIVITY_REQUEST_CODE: {
                mCameraHandler.takeCameraImage(this);
                break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // CameraHandler uses MainActivity to make requests. Redirect responses back to CameraHandler.
            case CameraHandler.CAMERA_ACTIVITY_REQUEST_CODE: {
                if (data == null) {
                    data = new Intent();
                }
                data.putExtra(CameraHandler.EXTRA_DATE_ITEM, mCurrentDateItem);
                mCameraHandler.onActivityResult(resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
