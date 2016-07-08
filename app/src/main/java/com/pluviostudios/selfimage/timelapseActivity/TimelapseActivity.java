package com.pluviostudios.selfimage.timelapseActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.dataContainers.date.DateItem;
import com.pluviostudios.selfimage.data.dataContainers.date.DateItemLoaderCallbacks;
import com.pluviostudios.selfimage.data.database.DatabaseContract;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by spectre on 6/30/16.
 */
public class TimelapseActivity extends AppCompatActivity {

    public static final String REFERENCE_ID = "TimelapseActivity";

    private static final int MAX = 5000;
    private static final int MIN = 5;
    private static final int INCREMENT = 5;

    private ViewFlipper mViewFlipper;
    private ImageView mImageView;
    private EditText mEditText;
    private Button mButtonAdd, mButtonSubtract, mButtonStart;

    private ArrayList<BitmapDrawable> mDrawables;
    private int mDuration = 250;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_timelapse);
        super.onCreate(savedInstanceState);

        mImageView = (ImageView) findViewById(R.id.activity_timelapse_imageview);
        mViewFlipper = (ViewFlipper) findViewById(R.id.activity_timelapse_viewflipper);
        mEditText = (EditText) findViewById(R.id.activity_timelapse_edit_text);
        mEditText.setText(String.valueOf(mDuration));

        mButtonAdd = (Button) findViewById(R.id.activity_timelapse_add);
        mButtonSubtract = (Button) findViewById(R.id.activity_timelapse_subtract);
        mButtonStart = (Button) findViewById(R.id.activity_timelapse_start);

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDuration < MAX) {
                    updateDuration(mDuration += INCREMENT);
                }
            }
        });

        mButtonSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDuration > MIN) {
                    updateDuration(mDuration -= INCREMENT);
                }
            }
        });

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(mEditText.getText().toString()) != mDuration) {
                    mDuration = Math.max(MIN, Math.min(Integer.parseInt(mEditText.getText().toString()), MAX));
                    updateDuration(mDuration);
                }
                buildAnimation();
            }
        });


        DateItem.getDateItems(getApplicationContext(), getSupportLoaderManager(), 0,
                DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL + " IS NOT NULL ",
                null,
                new DateItemLoaderCallbacks.OnDateItemsReceived() {
                    @Override
                    public void onDateItemsReceived(ArrayList<DateItem> dateItems) {

                        mDrawables = new ArrayList<>();

                        for (DateItem x : dateItems) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(x.img_dir));
                                BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                                mDrawables.add(drawable);

                            } catch (IOException e) {
                                Log.e(REFERENCE_ID, e.getMessage());
                            }
                        }

                        mImageView.setBackground(mDrawables.get(mDrawables.size() - 1));
                        mViewFlipper.showNext();

                    }
                }
        );


    }

    private void updateDuration(int duration) {
        mDuration = duration;
        mEditText.setText(String.valueOf(duration));
    }

    private void buildAnimation() {

        AnimationDrawable frameAnimation = new AnimationDrawable();
        frameAnimation.setOneShot(true);

        for (BitmapDrawable x : mDrawables) {
            frameAnimation.addFrame(x, mDuration);
        }

        mImageView.setImageDrawable(frameAnimation);
        frameAnimation.setVisible(true, true);

        frameAnimation.start();

    }

}



