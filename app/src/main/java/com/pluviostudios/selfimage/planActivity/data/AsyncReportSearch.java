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
public class AsyncReportSearch extends AsyncBaseData<ArrayList<FoodItemNetworkContainer>, FoodItemNetworkContainer, ArrayList<FoodItemNetworkContainer>> {

    private static final String REFERENCE_ID = "AsyncReportSearch";

    private OnAsyncReportSearchResult mOnAsyncReportSearchResult;

    public AsyncReportSearch(Context context, OnAsyncReportSearchResult onAsyncReportSearchResult) {
        super(context);
        mOnAsyncReportSearchResult = onAsyncReportSearchResult;
    }

    @Override
    protected ArrayList<FoodItemNetworkContainer> doInBackground(ArrayList<FoodItemNetworkContainer>... params) {

        ArrayList<FoodItemNetworkContainer> out = new ArrayList<>();

        for (FoodItemNetworkContainer x : params[0]) {

            x.status = FoodItemNetworkContainer.UPDATING;

            FoodItem currFoodItem = x.foodItem;

            Uri searchURI = BASE_URI_CAL.buildUpon()
                    .appendQueryParameter("ndbno", String.valueOf(currFoodItem.getNdbmo()))
                    .appendQueryParameter(API_PARAM, getContext().getString(R.string.api_key))
                    .build();

            Log.v(REFERENCE_ID, "[" + currFoodItem.getName() + "]");

            String foodData = getDataFromURL(REFERENCE_ID, searchURI.toString());
            if (foodData == null) {
                x.status = FoodItemNetworkContainer.ERROR;
                continue;
            }

            try {

                JSONObject root = new JSONObject(foodData);
                JSONObject report = root.getJSONObject("report");
                JSONObject food = report.getJSONObject("food");
                JSONArray nutrients = food.getJSONArray("nutrients");

                String debug = "    ";

                for (int i = 0; i < nutrients.length(); i++) {
                    JSONObject currObject = nutrients.getJSONObject(i);

                    int ID = currObject.optInt("nutrient_id");
                    for (int u = 0; u < FoodItem.nutrientIds.size(); u++) {
                        if (ID == FoodItem.nutrientIds.get(u)) {
                            debug += ("Val " + u + " : " + currObject.optDouble("value") + " | ");
                            currFoodItem.setValue(u, currObject.optDouble("value"));
                            break;
                        }
                    }

                }

                Log.v(REFERENCE_ID, debug);

                publishProgress(x);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return out;

    }

    @Override
    protected void onProgressUpdate(FoodItemNetworkContainer... values) {
        super.onProgressUpdate(values);
        if (mOnAsyncReportSearchResult != null) {
            values[0].status = FoodItemNetworkContainer.COMPLETE;
            mOnAsyncReportSearchResult.onResult(values[0]);
        }
    }

    public interface OnAsyncReportSearchResult {
        void onResult(FoodItemNetworkContainer data);
    }

}
