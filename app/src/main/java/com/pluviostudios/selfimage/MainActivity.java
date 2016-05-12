package com.pluviostudios.selfimage;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pluviostudios.selfimage.data.DataManager;
import com.pluviostudios.selfimage.data.DatabaseContract;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private DataManager mDataManager;

    private Button mButtonPrevious, mButtonNext;
    private TextView mPromptBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonNext = (Button) findViewById(R.id.button_next);
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousImage();
            }
        });

        mButtonPrevious = (Button) findViewById(R.id.button_last);
        mButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextImage();
            }
        });

        mPromptBar = (TextView) findViewById(R.id.activity_main_prompt_bar);

        // Check if today's image exists
        getSupportLoaderManager().initLoader(0, null, this);

    }

    private void displayPromptMessage(String message, int color) {
        mPromptBar.setText(message);
        mPromptBar.setBackgroundColor(color);
    }

    private void showPreviousImage() {

    }

    private void showNextImage() {

    }

    private void showImageDetails() {

    }

    private void launchMealDiaryActivity() {
        Intent startMealDiaryActivity = new Intent(this, MealPlanningActivity.class);
        startActivity(startMealDiaryActivity);
    }

    private void updatePicture() {

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(),
                DatabaseContract.DateEntry.buildDateWithStartDate(Calendar.getInstance().getTimeInMillis()),
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            displayPromptMessage(getString(R.string.has_current_image), Color.GREEN);
        } else {
            displayPromptMessage(getString(R.string.no_current_image), Color.RED);
        }
        data.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
