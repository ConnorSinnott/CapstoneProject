package com.pluviostudios.usdanutritionalapi;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/20/2016.
 */
public class AsyncFoodItemSearch extends AsyncBaseData<String, Void, ArrayList<FoodItem>> {

    private static final String REFERENCE_ID = "AsyncNDBNOSearch";

    private OnAsyncNDBNOSearchResult mOnAsyncNDBNOSearchResult;

    public AsyncFoodItemSearch(Context context, OnAsyncNDBNOSearchResult onAsyncNDBNOSearchResult) {
        super(context);
        this.mOnAsyncNDBNOSearchResult = onAsyncNDBNOSearchResult;
    }

    @Override
    protected ArrayList<FoodItem> doInBackground(String... params) {

        Uri searchURI = BASE_URI_NDBMO.buildUpon()
                .appendQueryParameter(API_PARAM, getContext().getString(R.string.api_key))
                .appendQueryParameter("q", params[0]).build();

        Log.v(REFERENCE_ID, "Searching for ndbno with args: " + params[0]);

        String ndbmoData = getDataFromURL(REFERENCE_ID, searchURI.toString());
        if (ndbmoData == null) {
            return null;
        }

        ArrayList<FoodItem> foodList = new ArrayList<>();

        try {

            JSONObject searchRoot = new JSONObject(ndbmoData);

            JSONObject list = searchRoot.getJSONObject("list");
            JSONArray listItems = list.getJSONArray("item");

            for (int i = 0; i < listItems.length(); i++) {
                JSONObject currObject = listItems.getJSONObject(i);

                FoodItem foodItem = new FoodItem(
                        currObject.optString("name"),
                        currObject.optString("ndbno"));

                foodList.add(foodItem);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v(REFERENCE_ID, "   Found " + foodList.size() + " relevant matches");

        return foodList;
    }

    @Override
    protected void onPostExecute(ArrayList<FoodItem> data) {
        if (data != null && mOnAsyncNDBNOSearchResult != null) {
            mOnAsyncNDBNOSearchResult.onResult(data);
        }
    }


    public interface OnAsyncNDBNOSearchResult {
        void onResult(ArrayList<FoodItem> data);
    }

}

