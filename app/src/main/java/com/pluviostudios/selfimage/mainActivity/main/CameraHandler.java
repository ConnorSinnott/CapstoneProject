package com.pluviostudios.selfimage.mainActivity.main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pluviostudios.selfimage.data.dataContainers.date.DateItem;
import com.pluviostudios.selfimage.utilities.MissingExtraException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Spectre on 5/19/2016.
 */
public class CameraHandler {

    public static final String REFERENCE_ID = "CameraHandler";
    public static final String EXTRA_DATE = "extra_date_item";
    public static final int CAMERA_ACTIVITY_REQUEST_CODE = 200;

    private static final SimpleDateFormat IMAGE_DATE_FORMAT = new SimpleDateFormat("MM-d-yyyy");
    private static final String[] REQUIRED_PERMISSIONS = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.CAMERA"};


    private AppCompatActivity mContext;
    private Uri mCurrentStorageUri;

    public CameraHandler(AppCompatActivity context) {
        mContext = context;
    }

    public void takeCameraImage(Activity context) {

        // Get permission to use the camera and access storage. Results will be rerouted from MainActivity
        for (String x : REQUIRED_PERMISSIONS) {
            if (context.checkSelfPermission(x) != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(REQUIRED_PERMISSIONS, CAMERA_ACTIVITY_REQUEST_CODE);
                return;
            }
        }

        // Launch the Camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(REFERENCE_ID, ex.getMessage());
            }

            if (photoFile != null) {
                // State where the image is to be saved
                mCurrentStorageUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentStorageUri);
                context.startActivityForResult(takePictureIntent, CAMERA_ACTIVITY_REQUEST_CODE);
            }

        }

    }

    public void onActivityResult(int resultCode, Intent data) {
        if (resultCode == MainActivity.RESULT_OK) {

            if (!data.hasExtra(EXTRA_DATE))
                throw new MissingExtraException(EXTRA_DATE);

            // Update the passed DateItem with the image info
            DateItem dateItem = (DateItem) data.getExtras().getSerializable(EXTRA_DATE);
            dateItem.img_dir = mCurrentStorageUri.toString();
            dateItem.save(mContext);

        }
    }

    public static File createImageFile() throws IOException {
        // Determine where the file is to be saved, and what it will be called

        String timeStamp = IMAGE_DATE_FORMAT.format(Calendar.getInstance().getTimeInMillis());
        String imageFileName = "SelfImg_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        if (image.exists()) {
            if (!image.delete()) {
                Log.e(REFERENCE_ID, "Unable to overwrite previous image");
            }
        }

        return image;
    }


}
