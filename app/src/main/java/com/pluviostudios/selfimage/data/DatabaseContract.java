package com.pluviostudios.selfimage.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.pluviostudios.selfimage.Utilities;

/**
 * Created by Spectre on 5/11/2016.
 */
public class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.pluviostudios.selfimage.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DATE = "date";
    public static final String PATH_DIARY = "diary";

    public static final class DateEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DATE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DATE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DATE;


        public static final String TABLE_NAME = PATH_DATE;

        public static final String DATE_COL = "date";
        public static final String IMAGE_DIRECTORY_COL = "img_dir";

        public static Uri buildDateUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildDateWithStartDate(long startDate) {
            long normalizedDate = Utilities.normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(normalizedDate)).build();
        }

        public static long getStartDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static android.support.v4.content.CursorLoader buildCursorLoaderWithDefaultProjection(Context context) {
            String[] projection = {
                    DatabaseContract.DateEntry._ID,
                    DatabaseContract.DateEntry.DATE_COL,
                    DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL
            };

            String sortOrder = DatabaseContract.DateEntry.DATE_COL + " ASC ";

            return new android.support.v4.content.CursorLoader(context,
                    DatabaseContract.DateEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
            );
        }

        public static int DEFAULT_COL_INDEX_ID = 0;
        public static int DEFAULT_COL_INDEX_DATE = 1;
        public static int DEFAULT_COL_INDEX_IMAGE_DIRECTORY = 2;

    }

    public static final class DiaryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DIARY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY;


        public static final String TABLE_NAME = PATH_DIARY;

        public static final String DATE_KEY_COL = "date_id";
        public static final String ITEM_NAME_COL = "item_name";
        public static final String ITEM_CAL_COL = "item_cal";

        public static Uri buildDiaryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildDiaryWithStartDate(long startDate) {
            long normalizedDate = Utilities.normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(startDate)).build();
        }

        public static long getStartDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }


}
