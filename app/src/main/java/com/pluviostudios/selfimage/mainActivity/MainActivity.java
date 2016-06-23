package com.pluviostudios.selfimage.mainActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.DatabaseContract;
import com.pluviostudios.selfimage.notification.DailyNotification;
import com.pluviostudios.selfimage.planActivity.MealPlanningActivity;
import com.pluviostudios.selfimage.utilities.CursorPagerAdapter;
import com.pluviostudios.selfimage.utilities.Utilities;
import com.viewpagerindicator.LinePageIndicator;

public class MainActivity extends AppCompatActivity {

    private static final String REFERENCE_ID = "MainActivity";

    private CameraHandler mCameraHandler;
    private TextView mPromptBar;
    private ViewPager mViewPager;
    private LinePageIndicator mTabPageIndicator;
    private FrameLayout mBottomFrame;
    private CalorieBar mCalBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPromptBar = (TextView) findViewById(R.id.activity_main_prompt_bar);
        mViewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        mTabPageIndicator = (LinePageIndicator) findViewById(R.id.activity_main_tab_page_indicator);
        mBottomFrame = (FrameLayout) findViewById(R.id.activity_main_bottom_frame);
        mCalBar = (CalorieBar) findViewById(R.id.activity_main_calorie_bar);

        final String CreateFoodTable = "CREATE TABLE " + DatabaseContract.FoodEntry.TABLE_NAME + " ("
                + DatabaseContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.FoodEntry.ITEM_NDBNO_COL + " TEXT NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_NAME_COL + " TEXT NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_LAST_ACCESSED + " LONG NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_CALORIE_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_PROTEIN_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_FAT_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_CARBS_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_FIBER_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_SATFAT_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_MONOFAT_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_POLYFAT_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_CHOLESTEROL_COL + " REAL NOT NULL, "
                + " UNIQUE (" + DatabaseContract.FoodEntry.ITEM_NDBNO_COL + ") ON CONFLICT ABORT) ";

        Log.i(REFERENCE_ID, CreateFoodTable);

        mCameraHandler = new CameraHandler(this);

        DayCardFragment.setOnFABClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraHandler.takeCameraImage(MainActivity.this);
            }
        });

        mBottomFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MealPlanningActivity.buildMealPlanActivityIntent(
                        MainActivity.this,
                        Utilities.getCurrentNormalizedDate()));
            }
        });

        getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {

            private final String[] projection = {
                    DatabaseContract.DateEntry.DATE_COL,
                    DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL
            };

            private final String mainActivityQuerySortOrder = DatabaseContract.DateEntry.DATE_COL + " ASC ";

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(MainActivity.this,
                        DatabaseContract.DateEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        mainActivityQuerySortOrder
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                setDayData(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });

        getSupportLoaderManager().initLoader(1, null, new LoaderManager.LoaderCallbacks<Cursor>() {

            private final String[] projection = {
                    DatabaseContract.FoodEntry.ITEM_CALORIE_COL,
                    DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL
            };

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(MainActivity.this,
                        DatabaseContract.DiaryEntry.buildDiaryWithStartDate(Utilities.getCurrentNormalizedDate()),
                        projection,
                        null,
                        null,
                        null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                updateCalories(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });

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


    protected void updateCalories(Cursor data) {
        int cal = 0;
        if (data.moveToFirst()) {
            do {
                cal += (data.getInt(0) * data.getInt(1));
            } while (data.moveToNext());
        }
        mCalBar.setProgress(cal);
    }

    protected void displayPromptMessage(String message, int color) {
        mPromptBar.setText(message);
        mPromptBar.setBackgroundColor(color);
    }

    protected void setDayData(Cursor data) {

        long currentDate = Utilities.getCurrentNormalizedDate();

        long processedDate = 0;
        if (data.moveToLast()) {
            // What is the most recent date we have in SQL?
            processedDate = Long.parseLong(data.getString(0));
        }

        boolean hasCurrentSlot = processedDate == currentDate;
        if (!hasCurrentSlot) {
            // Create a slot for today in SQL, image is null
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.DateEntry.DATE_COL, currentDate);
            getContentResolver().insert(DatabaseContract.DateEntry.CONTENT_URI, contentValues);
            return; // Will trigger a refresh
        }

        boolean hasCurrentImage = !data.isNull(1);
        displayPromptMessage(
                hasCurrentImage ?
                        getString(R.string.has_current_image) :
                        getString(R.string.no_current_image),
                hasCurrentImage ?
                        Color.GREEN :
                        Color.RED
        );

        // Implement the ImagePagerAdapter
        // TODO Update without creating a new adapter
        mViewPager.setAdapter(new CursorPagerAdapter<>(getSupportFragmentManager(),
                DayCardFragment.class,
                new String[]{
                        DatabaseContract.DateEntry.DATE_COL,
                        DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL},
                data));

        // Setup the ImagePagerAdapter's Indicator
        mTabPageIndicator.setViewPager(mViewPager);

        // Set the current page to the most recent image;
        mViewPager.setCurrentItem(data.getCount() - 1);

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
                mCameraHandler.onActivityResult(resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
