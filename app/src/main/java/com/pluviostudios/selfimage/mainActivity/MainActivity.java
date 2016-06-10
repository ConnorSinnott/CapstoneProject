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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.DatabaseContract;
import com.pluviostudios.selfimage.planActivity.activity.MealPlanningActivity;
import com.pluviostudios.selfimage.utilities.CursorPagerAdapter;
import com.pluviostudios.selfimage.utilities.Utilities;
import com.viewpagerindicator.LinePageIndicator;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String REFERENCE_ID = "MainActivity";

    private CameraHandler mCameraHandler;
    private TextView mPromptBar;
    private ViewPager mViewPager;
    private LinePageIndicator mTabPageIndicator;
    private FrameLayout mBottomFrame;

    private static final String[] mainActivityQueryProjection = {
            DatabaseContract.DateEntry.DATE_COL,
            DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL
    };
    private static final String mainActivityQuerySortOrder = DatabaseContract.DateEntry.DATE_COL + " ASC ";
    private static final int mainActivityQueryIndexDate = 0;
    private static final int mainActivityQueryIndexImageDir = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPromptBar = (TextView) findViewById(R.id.activity_main_prompt_bar);
        mViewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        mTabPageIndicator = (LinePageIndicator) findViewById(R.id.activity_main_tab_page_indicator);
        mBottomFrame = (FrameLayout) findViewById(R.id.activity_main_bottom_frame);

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
                Intent intent = new Intent(MainActivity.this, MealPlanningActivity.class);
                startActivity(intent);
            }
        });

        final String CreateDateTable = "CREATE TABLE " + DatabaseContract.DateEntry.TABLE_NAME + " ("
                + DatabaseContract.DateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.DateEntry.DATE_COL + " LONG NOT NULL,"
                + DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL + " TEXT, "
                + " UNIQUE (" + DatabaseContract.DateEntry.DATE_COL + ") ON CONFLICT REPLACE)";

        final String CreateDiaryTable = "CREATE TABLE " + DatabaseContract.DiaryEntry.TABLE_NAME + " ("
                + DatabaseContract.DiaryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.DiaryEntry.DATE_COL + " LONG NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_NAME_COL + " TEXT NOT NULL ,"
                + DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL + " INTEGER NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_NDBNO_COL + " TEXT NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL + " INTEGER NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_CALORIE_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_PROTEIN_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_FAT_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_CARBS_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_FIBER_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_SATFAT_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_MONOFAT_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_POLYFAT_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_CHOLESTEROL_COL + " REAL NOT NULL, "
                + " FOREIGN KEY (" + DatabaseContract.DiaryEntry.DATE_COL + ") REFERENCES "
                + DatabaseContract.DateEntry.TABLE_NAME + " (" + DatabaseContract.DateEntry.DATE_COL + ")"
                + " FOREIGN KEY (" + DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL + ") REFERENCES "
                + DatabaseContract.CategoryEntry.TABLE_NAME + "(" + DatabaseContract.CategoryEntry._ID + ")"
                + ")";

        final String CreateCategoryTable = "CREATE TABLE " + DatabaseContract.CategoryEntry.TABLE_NAME + " ("
                + DatabaseContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.CategoryEntry.CATEGORY_INDEX_COL + " INTEGER UNIQUE NOT NULL, "
                + DatabaseContract.CategoryEntry.CATEGORY_NAME_COL + " TEXT UNIQUE NOT NULL, "
                + " UNIQUE (" + DatabaseContract.CategoryEntry.CATEGORY_NAME_COL + " ) ON CONFLICT ABORT)";

        Log.d("TEST", CreateDiaryTable);


        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void displayPromptMessage(String message, int color) {
        mPromptBar.setText(message);
        mPromptBar.setBackgroundColor(color);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                DatabaseContract.DateEntry.CONTENT_URI,
                mainActivityQueryProjection,
                null,
                null,
                mainActivityQuerySortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        long currentDate = Utilities.getCurrentNormalizedDate();

        long processedDate = 0;
        if (data.moveToLast()) {
            // What is the most recent date we have in SQL?
            processedDate = Long.parseLong(data.getString(mainActivityQueryIndexDate));
        }

        boolean hasCurrentSlot = processedDate == currentDate;
        if (!hasCurrentSlot) {
            // Create a slot for today in SQL, image is null
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.DateEntry.DATE_COL, currentDate);
            getContentResolver().insert(DatabaseContract.DateEntry.CONTENT_URI, contentValues);
            return; // Will trigger a refresh
        }

        boolean hasCurrentImage = !data.isNull(mainActivityQueryIndexImageDir);
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
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
