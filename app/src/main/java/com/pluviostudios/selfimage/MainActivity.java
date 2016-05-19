package com.pluviostudios.selfimage;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.pluviostudios.selfimage.data.DatabaseContract;
import com.viewpagerindicator.LinePageIndicator;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CAMERA_ACTIVITY_REQUEST_CODE = 200;

    public static long sCurrentDate = 0;
    private Uri mCurrentStorageUri;

    private TextView mPromptBar;
    private ViewPager mViewPager;
    private LinePageIndicator mTabPageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPromptBar = (TextView) findViewById(R.id.activity_main_prompt_bar);
        mViewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        mTabPageIndicator = (LinePageIndicator) findViewById(R.id.activity_main_tab_page_indicator);

        sCurrentDate = Utilities.getCurrentNormalizedDate();

        populateSliders();
    }


    private void populateSliders() {
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void takeCameraImage() {
        String[] perms = {
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.CAMERA"};

        for (String x : perms) {
            if (checkSelfPermission(x) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(perms, CAMERA_ACTIVITY_REQUEST_CODE);
                return;
            }
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = Utilities.createImageFile();
            } catch (IOException ex) {
                Logger.init();
                Logger.wtf(ex.getMessage());
            }

            if (photoFile != null) {
                mCurrentStorageUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAMERA_ACTIVITY_REQUEST_CODE);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL, mCurrentStorageUri.toString());
                getContentResolver().update(
                        DatabaseContract.DateEntry.CONTENT_URI, contentValues,
                        DatabaseContract.DateEntry.DATE_COL + " = ?", new String[]{String.valueOf(sCurrentDate)}
                );
                getSupportLoaderManager().restartLoader(0, null, this);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_ACTIVITY_REQUEST_CODE: {
                takeCameraImage();
                break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void displayPromptMessage(String message, int color) {
        mPromptBar.setText(message);
        mPromptBar.setBackgroundColor(color);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return DatabaseContract.DateEntry.buildCursorLoaderWithDefaultProjection(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        long processedDate = 0;
        if (data.moveToLast()) {
            // What is the most recent date we have in SQL?
            processedDate = Long.parseLong(data.getString(DatabaseContract.DateEntry.DEFAULT_COL_INDEX_DATE));
        }

        // If that date is today
        if (processedDate == sCurrentDate
                && !data.isNull(DatabaseContract.DateEntry.DEFAULT_COL_INDEX_IMAGE_DIRECTORY)) {
            // You are all set
            displayPromptMessage(getString(R.string.has_current_image), Color.GREEN);
        } else {
            // Otherwise
            displayPromptMessage(getString(R.string.no_current_image), Color.RED);

            // Create a slot for today in SQL, image is null
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.DateEntry.DATE_COL, sCurrentDate);
            getContentResolver().insert(DatabaseContract.DateEntry.CONTENT_URI, contentValues);
        }


        // Setup the ImagePagerAdapter
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(this, getSupportFragmentManager(), data);
        imagePagerAdapter.setOnFABClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeCameraImage();
            }
        });

        // Implement the ImagePagerAdapter
        mViewPager.setAdapter(imagePagerAdapter);

        // Setup the ImagePagerAdapter's Indicator
        mTabPageIndicator.setViewPager(mViewPager);

        // Set the current page to the most recent image;
        mViewPager.setCurrentItem(data.getCount() - 1);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
