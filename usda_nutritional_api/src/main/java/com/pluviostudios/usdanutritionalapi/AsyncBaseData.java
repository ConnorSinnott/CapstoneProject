package com.pluviostudios.usdanutritionalapi;

import android.net.Uri;

/**
 * Created by Spectre on 6/20/2016.
 */
public abstract class AsyncBaseData<V, V1, V2> extends AsyncWithGetData<V, V1, V2> {

    protected String mAPIKey;
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

    public AsyncBaseData(String APIKey) {
        mAPIKey = APIKey;
    }

}
