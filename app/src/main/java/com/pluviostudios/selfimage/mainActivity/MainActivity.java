package com.pluviostudios.selfimage.mainActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.dataContainers.date.DateItem;
import com.pluviostudios.selfimage.data.dataContainers.date.DateItemLoaderCallbacks;
import com.pluviostudios.selfimage.data.database.DBHelper;
import com.pluviostudios.selfimage.mainActivity.planning.MealPlanFragment;
import com.pluviostudios.selfimage.notification.DailyNotification;
import com.pluviostudios.selfimage.utilities.DateUtils;
import com.pluviostudios.selfimage.utilities.SettingsActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.viewpagerindicator.LinePageIndicator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String REFERENCE_ID = "MainActivity";

    private DateItem mCurrentDateItem;
    private ArrayList<DateItem> mDateItems;

    private CameraHandler mCameraHandler;
    private TextView mPromptBar;
    private ViewPager mViewPager;
    private LinePageIndicator mTabPageIndicator;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPromptBar = (TextView) findViewById(R.id.activity_main_prompt_bar);
        mViewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        mTabPageIndicator = (LinePageIndicator) findViewById(R.id.activity_main_tab_page_indicator);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.activity_main_sliding_up_panel);

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

        DailyNotification.initialize(getApplicationContext());

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
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.options_test_notify:
                Snackbar.make(mSlidingUpPanelLayout, "Sending notification in 10 seconds", Snackbar.LENGTH_SHORT).show();
                DailyNotification.testNotification(this, 10);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void displayPromptMessage(String message, int color) {
        mPromptBar.setText(message);
        mPromptBar.setBackgroundColor(color);
    }

    private void addNewDay() {
        DateItem newDateItem = new DateItem(DateUtils.getCurrentNormalizedDate(), null);
        newDateItem.save(getApplicationContext());
    }

    private void showPlanFragment(int position) {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.activity_main_meal_plan_frame,
                MealPlanFragment.buildMealPlanFragment(mDateItems.get(position).date)
        ).commitAllowingStateLoss();
    }

    protected void setDayData(ArrayList<DateItem> dateItems) {

        if (dateItems.size() == 0) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage("Hello Tester! The database is empty. Would you like to add dummy data?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DBHelper.addDummyData(getApplicationContext());
                            addNewDay();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addNewDay();
                        }
                    }).show();
            return;
        }

        mDateItems = dateItems;
        mCurrentDateItem = mDateItems.get(dateItems.size() - 1);

        if (!DateUtils.isTodaysDate(mCurrentDateItem.date)) {
            addNewDay();
        }

        displayPromptMessage(
                mCurrentDateItem.hasCurrentImage() ?
                        getString(R.string.has_current_image) :
                        getString(R.string.no_current_image),
                mCurrentDateItem.hasCurrentImage() ?
                        getColor(R.color.has_today) :
                        getColor(R.color.needs_today)
        );

        // Implement the ImagePagerAdapter
        mViewPager.setAdapter(new DayCardPagerAdapter(getSupportFragmentManager(), dateItems));
        if (mDateItems.size() == 1) {
            showPlanFragment(0);
        } else {
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    showPlanFragment(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

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
