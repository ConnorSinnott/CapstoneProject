package com.pluviostudios.selfimage.mainActivity.main;

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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.dataContainers.date.DateItem;
import com.pluviostudios.selfimage.data.dataContainers.date.DateItemLoaderCallbacks;
import com.pluviostudios.selfimage.data.database.DBHelper;
import com.pluviostudios.selfimage.mainActivity.planning.MealPlanFragment;
import com.pluviostudios.selfimage.notification.DailyNotification;
import com.pluviostudios.selfimage.timelapseActivity.TimelapseActivity;
import com.pluviostudios.selfimage.utilities.DateUtils;
import com.pluviostudios.selfimage.utilities.SettingsActivity;
import com.pluviostudios.selfimage.widget.CalBarProvider;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.viewpagerindicator.LinePageIndicator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String REFERENCE_ID = "MainActivity";

    private CameraHandler mCameraHandler;
    private DateItem mCurrentDateItem;
    private ArrayList<DateItem> mDateItems;

    private TextView mPromptBar;
    private ViewPager mViewPager;
    private LinePageIndicator mTabPageIndicator;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize
        mPromptBar = (TextView) findViewById(R.id.activity_main_prompt_bar);
        mViewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        mTabPageIndicator = (LinePageIndicator) findViewById(R.id.activity_main_tab_page_indicator);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.activity_main_sliding_up_panel);

        // Create an instance of CameraHandler, this must be passed data from OnActivityResult
        mCameraHandler = new CameraHandler(MainActivity.this);

        // FAB -> Take image of user and save it to mCurrentDateItem
        DayCardFragment.setOnFABClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraHandler.takeCameraImage(MainActivity.this);
            }
        });

        // Start a loader for all DayItems in database, send resulting list to setDayData()
        DateItem.getDateItems(this, getSupportLoaderManager(), 0, null, null, new DateItemLoaderCallbacks.OnDateItemsReceived() {
            @Override
            public void onDateItemsReceived(ArrayList<DateItem> dateItems) {
                setDayData(dateItems);
            }
        });

        // Initialize DailyNotification. This will schedule a notification to be deployed in the future
        DailyNotification.initialize(getApplicationContext());

    }

    protected void setDayData(ArrayList<DateItem> dateItems) {

        // If the database has no DateItems, assume this is the first launch
        // Todo Remove for production
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

        // Create a DateItem for today if it does not exist
        if (!DateUtils.isTodaysDate(mCurrentDateItem.date)) {
            addNewDay();
        }

        // Update the SelfImage prompt bar
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

            // If the ViewPager has only one item, onPageSelected will not be called on start
            showPlanFragment(0);

        } else {

            // Otherwise, switch the CalBar to reflect the DayItem currently being viewed
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    // Not Needed
                }

                @Override
                public void onPageSelected(int position) {
                    showPlanFragment(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    // Not Needed
                }
            });

        }

        // Setup the ImagePagerAdapter's Indicator
        mTabPageIndicator.setViewPager(mViewPager);

        // Set the current page to the most recent image;
        mViewPager.setCurrentItem(dateItems.size() - 1);

    }

    protected void displayPromptMessage(String message, int color) {
        mPromptBar.setText(message);
        mPromptBar.setBackgroundColor(color);
    }

    private void addNewDay() {
        // Create a new DateItem with today's date and save it
        DateItem newDateItem = new DateItem(DateUtils.getCurrentNormalizedDate(), null);
        newDateItem.save(getApplicationContext());
    }

    private void showPlanFragment(int position) {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.activity_main_meal_plan_frame,
                MealPlanFragment.buildMealPlanFragment(mDateItems.get(position).date)
        ).commitAllowingStateLoss();
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

            // Redirect result data to CameraHandler
            case CameraHandler.CAMERA_ACTIVITY_REQUEST_CODE: {

                if (data == null)
                    data = new Intent();

                // Add instance of mCurrentDateItem into intent extras so it can be updated
                data.putExtra(CameraHandler.EXTRA_DATE, mCurrentDateItem);
                mCameraHandler.onActivityResult(resultCode, data);
                break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            case R.id.options_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            }
//            case R.id.options_timelapse: {
//                mInterstitialAd = new InterstitialAd(this);
//                mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//                mInterstitialAd.setAdListener(new AdListener() {
//
//                    @Override
//                    public void onAdLoaded() {
//                        mInterstitialAd.show();
//                    }
//
//                    @Override
//                    public void onAdClosed() {
//                        Intent intent = new Intent(getApplicationContext(), TimelapseActivity.class);
//                        startActivity(intent);
//                    }
//                });
//
//                requestNewInterstitial();
//                break;
//
//            }
            case R.id.options_timelapse: {
                Intent intent = new Intent(getApplicationContext(), TimelapseActivity.class);
                startActivity(intent);
            }
            case R.id.options_test_notify: {
                Snackbar.make(mSlidingUpPanelLayout, "Sending notification in 10 seconds", Snackbar.LENGTH_SHORT).show();
                DailyNotification.testNotification(this, 10);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        //BackButton -> close the SlidingUpPanel if it is expanded
        if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        CalBarProvider.updateWidget(this);
        super.onStop();
    }
}
