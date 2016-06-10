package com.pluviostudios.selfimage.planActivity.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.pluviostudios.selfimage.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Spectre on 5/26/2016.
 */
public class AsyncNDBNOSearch extends AsyncBaseData<String, Void, ArrayList<FoodItemNetworkContainer>> {

    private static final String REFERENCE_ID = "AsyncNDBNOSearch";

    private OnAsyncNDBNOSearchResult mOnAsyncNDBNOSearchResult;

    public AsyncNDBNOSearch(Context context, OnAsyncNDBNOSearchResult onAsyncNDBNOSearchResult) {
        super(context);
        this.mOnAsyncNDBNOSearchResult = onAsyncNDBNOSearchResult;
    }

    @Override
    protected ArrayList<FoodItemNetworkContainer> doInBackground(String... params) {

        Uri searchURI = BASE_URI_NDBMO.buildUpon()
                .appendQueryParameter(API_PARAM, getContext().getString(R.string.api_key))
                .appendQueryParameter("q", params[0]).build();

        Log.v(REFERENCE_ID, "Searching for ndbno with args: " + params[0]);

        String ndbmoData = getDataFromURL(REFERENCE_ID, searchURI.toString());
        if (ndbmoData == null) {
            return null;
        }

        ArrayList<FoodItemNetworkContainer> ndbmoList = new ArrayList<>();

        try {

            JSONObject searchRoot = new JSONObject(ndbmoData);

            JSONObject list = searchRoot.getJSONObject("list");
            JSONArray listItems = list.getJSONArray("item");

            for (int i = 0; i < listItems.length(); i++) {
                JSONObject currObject = listItems.getJSONObject(i);

                FoodItemNetworkContainer newItem = new FoodItemNetworkContainer(
                        new FoodItem(
                                currObject.optString("name"),
                                currObject.optString("ndbno")));
                ndbmoList.add(newItem);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v(REFERENCE_ID, "   Found " + ndbmoList.size() + " relevant matches");

        return ndbmoList;
    }

    @Override
    protected void onPostExecute(ArrayList<FoodItemNetworkContainer> foodItems) {
        if (foodItems != null && mOnAsyncNDBNOSearchResult != null) {
            mOnAsyncNDBNOSearchResult.onResult(foodItems);
        }
    }

    public interface OnAsyncNDBNOSearchResult {
        void onResult(ArrayList<FoodItemNetworkContainer> data);
    }

}
