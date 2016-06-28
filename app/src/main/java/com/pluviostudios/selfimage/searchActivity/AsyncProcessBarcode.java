package com.pluviostudios.selfimage.searchActivity;

import android.net.Uri;
import android.util.Log;

import com.pluviostudios.usdanutritionalapi.AsyncWithGetData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Spectre on 6/23/2016.
 */
public class AsyncProcessBarcode extends AsyncWithGetData<String, Void, String> {

    private static final String REFERENCE_ID = "AsyncProcessBarcode";

    private String api;
    private OnBarcodeProcessed mOnBarcodeProcessed;

    private String mSearchUPC;

    private static final String API_PARAM = "apikey";

    private Uri BASE_URI = new Uri.Builder().scheme("http").authority("api.outpan.com")
            .appendPath("ndb")
            .appendPath("v2")
            .appendPath("products")
            .appendQueryParameter("format", "json")
            .build();

    public AsyncProcessBarcode(String api, OnBarcodeProcessed onBarcodeProcessed) {
        this.api = api;
        mOnBarcodeProcessed = onBarcodeProcessed;
    }

    @Override
    protected String doInBackground(String... params) {

        mSearchUPC = params[0];

        Uri searchUri = BASE_URI.buildUpon()
                .appendPath(mSearchUPC)
                .appendQueryParameter(API_PARAM, api).build();

        String out = getDataFromURL(REFERENCE_ID, searchUri.toString());

        if (out != null) {
            try {
                JSONObject jsonObject = new JSONObject(out);
                return jsonObject.optString("name");
            } catch (JSONException e) {
                Log.e(REFERENCE_ID, e.getMessage());
                return null;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s.equals("null")) {
            s = null;
        }
        mOnBarcodeProcessed.onBarcodeProcessed(mSearchUPC, s);
    }

    public interface OnBarcodeProcessed {
        void onBarcodeProcessed(String UPC, String itemName);
    }
}
