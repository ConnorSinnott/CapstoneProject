package com.pluviostudios.usdanutritionalapi;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Spectre on 6/9/2016.
 */
public class FoodItem implements Serializable {

    //If params are added upon please update DB
    public static final int Calories = 0;
    public static final int Protein = 1;
    public static final int Fat = 2;
    public static final int Carbs = 3;
    public static final int Fiber = 4;
    public static final int SatFat = 5;
    public static final int MonoFat = 6;
    public static final int PolyFat = 7;
    public static final int Cholesterol = 8;

    public static final ArrayList<Integer> nutrientIds = new ArrayList<>();

    static {
        nutrientIds.add(Calories, 208);
        nutrientIds.add(Protein, 203);
        nutrientIds.add(Fat, 204);
        nutrientIds.add(Carbs, 205);
        nutrientIds.add(Fiber, 291);
        nutrientIds.add(SatFat, 606);
        nutrientIds.add(MonoFat, 645);
        nutrientIds.add(PolyFat, 646);
        nutrientIds.add(Cholesterol, 601);
    }

    private String foodName;
    private String foodNDBNO;
    private ArrayList<Double> mNutrientData = null;

    public FoodItem(String foodName, String foodNDBNO) {
        this.foodName = foodName;
        this.foodNDBNO = foodNDBNO;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodNDBNO() {
        return foodNDBNO;
    }

    public boolean hasNutrientData() {
        return mNutrientData != null;
    }

    public ArrayList<Double> getNutrientData() {
        return mNutrientData;
    }

    public void putNutrientData(ArrayList<Double> data) {
        mNutrientData = data;
    }

    public void pullNutrientData(String apiKey, OnDataPulled onDataPulled) {
        new AsyncReportSearch(apiKey, onDataPulled).execute(foodNDBNO);
    }

    public interface OnDataPulled {
        void OnDataPulled(FoodItem foodItem);
    }

    private final class AsyncReportSearch extends AsyncBaseData<String, Void, ArrayList<Double>> {

        private static final String REFERENCE_ID = "AsyncReportSearch";

        private OnDataPulled mOnDataPulled;

        public AsyncReportSearch(String APIKey, OnDataPulled onDataPulled) {
            super(APIKey);
            mOnDataPulled = onDataPulled;
        }

        @Override
        protected ArrayList<Double> doInBackground(String... params) {

            ArrayList<Double> out = new ArrayList<>();

            Uri searchURI = BASE_URI_CAL.buildUpon()
                    .appendQueryParameter(API_PARAM, mAPIKey)
                    .appendQueryParameter("ndbno", String.valueOf(params[0]))
                    .build();

            Log.v(REFERENCE_ID, "[" + params[0] + "]");

            String foodData = getDataFromURL(REFERENCE_ID, searchURI.toString());
            if (foodData == null) {
                return null;
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
                    for (int u = 0; u < nutrientIds.size(); u++) {
                        if (ID == nutrientIds.get(u)) {
                            debug += ("Val " + u + " : " + currObject.optDouble("value") + " | ");
                            out.add(u, currObject.optDouble("value", -1.0));
                            break;
                        }
                    }
                    out.add(-1.0);

                }

                Log.v(REFERENCE_ID, debug);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return out;

        }

        @Override
        protected void onPostExecute(ArrayList<Double> doubles) {
            mNutrientData = doubles;
            if (mOnDataPulled != null) {
                mOnDataPulled.OnDataPulled(FoodItem.this);
            }
        }

    }

}
