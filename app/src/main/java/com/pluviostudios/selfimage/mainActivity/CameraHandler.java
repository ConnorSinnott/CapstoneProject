package com.pluviostudios.selfimage.mainActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pluviostudios.selfimage.data.dataContainers.date.DateItem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Spectre on 5/19/2016.
 */
public class CameraHandler {

    private static final String REFERENCE_ID = "CameraHandler";

    public static final String EXTRA_DATE_ITEM = "extra_date_item";

    public static final int CAMERA_ACTIVITY_REQUEST_CODE = 200;

    private AppCompatActivity mContext;
    private Uri mCurrentStorageUri;
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MM-d-yyyy");

    private static final String[] REQUIRED_PERMISSIONS = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.CAMERA"};


    public CameraHandler(AppCompatActivity context) {
        mContext = context;
    }

    public void takeCameraImage(Activity context) {

        for (String x : REQUIRED_PERMISSIONS) {
            if (context.checkSelfPermission(x) != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(REQUIRED_PERMISSIONS, CAMERA_ACTIVITY_REQUEST_CODE);
                return;
            }
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(REFERENCE_ID, ex.getMessage());
            }

            if (photoFile != null) {
                mCurrentStorageUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentStorageUri);
                context.startActivityForResult(takePictureIntent, CAMERA_ACTIVITY_REQUEST_CODE);
            }

        }

    }

    public boolean onActivityResult(int resultCode, Intent data) {

        if (resultCode == mContext.RESULT_OK) {


            DateItem dateItem = (DateItem) data.getExtras().getSerializable(EXTRA_DATE_ITEM);
            dateItem.img_dir = mCurrentStorageUri.toString();
            dateItem.save(mContext);

//            ContentValues contentValues = new ContentValues();
//            contentValues.put(DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL, );
//            mContext.getContentResolver().update(
//                    DatabaseContract.DateEntry.CONTENT_URI, contentValues,
//                    DatabaseContract.DateEntry.DATE_COL + " = ?", new String[]{String.valueOf(DateUtils.getCurrentNormalizedDate())}
//            );
            return true;
        } else if (resultCode == mContext.RESULT_CANCELED) {
            return false;
            // User cancelled the image capture
        } else {
            return false;
            // Image capture failed, advise user
        }

    }

    public static File createImageFile() throws IOException {
        String timeStamp = mSimpleDateFormat.format(Calendar.getInstance().getTimeInMillis());
        String imageFileName = "SelfImg_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        if (image.exists()) {
            image.delete();
        }

        Log.d(REFERENCE_ID, image.getAbsolutePath());

        return image;
    }


}
