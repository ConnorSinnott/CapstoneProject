package com.pluviostudios.usdanutritionalapi;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Spectre on 6/23/2016.
 */
public abstract class AsyncWithGetData<V, V1, V2> extends AsyncTask<V, V1, V2> {

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
