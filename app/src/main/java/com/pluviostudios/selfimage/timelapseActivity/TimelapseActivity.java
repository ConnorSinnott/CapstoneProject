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

    private ViewFlipper mViewFlipper;
    private ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_timelapse);
        super.onCreate(savedInstanceState);

        mImageView = (ImageView) findViewById(R.id.activity_timelapse_imageview);
        mViewFlipper = (ViewFlipper) findViewById(R.id.activity_timelapse_viewflipper);

        DateItem.getDateItems(getApplicationContext(), getSupportLoaderManager(), 0,
                DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL + " IS NOT NULL ",
                null,
                new DateItemLoaderCallbacks.OnDateItemsReceived() {
                    @Override
                    public void onDateItemsReceived(ArrayList<DateItem> dateItems) {

                        AnimationDrawable frameAnimation = new AnimationDrawable();
                        frameAnimation.setOneShot(false);

                        int reasonableDuration = 250;

                        for (DateItem x : dateItems) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(x.img_dir));
                                BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                                frameAnimation.addFrame(drawable, reasonableDuration);
                            } catch (IOException e) {
                                Log.e(REFERENCE_ID, e.getMessage());
                            }
                        }

                        mImageView.setBackgroundDrawable(frameAnimation);
                        frameAnimation.setVisible(true, true);

                        mViewFlipper.showNext();

                        frameAnimation.start();

                    }
                });

    }

}



