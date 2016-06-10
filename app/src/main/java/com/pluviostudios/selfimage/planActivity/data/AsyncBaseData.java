package com.pluviostudios.selfimage.planActivity.data;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Spectre on 5/26/2016.
 */
public abstract class AsyncBaseData<V, V1, V2> extends AsyncTask<V, V1, V2> {

    private Context mContext;

    protected static final String API_PARAM = "api_key";

    protected static Uri BASE_URI = new Uri.Builder().scheme("http").authority("api.nal.usda.gov").appendPath("ndb")
            .appendQueryParameter("format", "json")
            .build();

    protected static Uri BASE_URI_NDBMO = BASE_URI.buildUpon().appendPath("search")
            .appendQueryParameter("max", "10")
            .build();

    protected static Uri BASE_URI_CAL = BASE_URI.buildUpon().appendPath("reports")
            .appendQueryParameter("name", "Energy")
            .appendQueryParameter("type", "b")
            .build();

    public AsyncBaseData(Context context) {
        mContext = context;
    }

    protected Context getContext() {
        return mContext;
    }

    protected String getDataFromURL(String referenceID, String address) {

        Log.v(referenceID, "   " + address);

        try {
            URL url = new URL(address);

            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {

                String out = "";

                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();

                int bytesRead;
                byte[] buffer = new byte[100];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out += new String(buffer, 0, bytesRead);
                }

                inputStream.close();

                return out;

            } else {
                Log.e(referenceID, "    No file to download. Server replied HTTP code: " + responseCode);
            }
            httpConn.disconnect();


        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }

        return null;

    }

}
