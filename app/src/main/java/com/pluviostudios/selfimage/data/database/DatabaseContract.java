package com.pluviostudios.selfimage.data.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.pluviostudios.selfimage.utilities.DateUtils;

/**
 * Created by Spectre on 5/11/2016.
 */
public class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.pluviostudios.selfimage.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DATE = "date";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_DIARY = "diary";
    public static final String PATH_FOOD = "food";

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
            long normalizedDate = DateUtils.normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(normalizedDate)).build();
        }

        public static long getStartDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

    }

    public static final class CategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        public static final String TABLE_NAME = "category";

        public static final String CATEGORY_INDEX_COL = "category_index";
        public static final String CATEGORY_NAME_COL = "category_name";

        public static Uri buildCategoryWithIndex(int index) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(index)).build();
        }

        public static int getIndexFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

    public static final class DiaryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DIARY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY;


        public static final String TABLE_NAME = PATH_DIARY;

        public static final String ITEM_DATE_COL = "item_date_key";
        public static final String ITEM_CATEGORY_COL = "item_category_key";
        public static final String ITEM_QUANTITY_COL = "item_quantity";
        public static final String ITEM_NDBNO_COL = "item_ndbno_key";

        public static Uri buildDiaryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

        public static Uri buildDiaryWithStartDate(long startDate) {
            long normalizedDate = DateUtils.normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(normalizedDate)).build();
        }

        public static Uri buildDiaryWithStartDateAndCategory(long startDate, int category) {
            return buildDiaryWithStartDate(startDate).buildUpon()
                    .appendPath(String.valueOf(category))
                    .build();
        }

        public static Uri buildDiaryWithStartDateAndCategoryAndNDBNO(long startDate, String ndbno, int category) {
            return buildDiaryWithStartDateAndCategory(startDate, category).buildUpon()
                    .appendPath(ndbno)
                    .build();
        }

        public static long getStartDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static int getCategoryFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(2));
        }

        public static String getNDBNOFromUri(Uri uri) {
            return uri.getPathSegments().get(3);
        }
    }

    public static final class FoodEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FOOD).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOOD;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOOD;


        public static final String TABLE_NAME = PATH_FOOD;

        public static final String ITEM_NDBNO_COL = "item_ndbno";
        public static final String ITEM_NAME_COL = "item_name";
        public static final String ITEM_LAST_ACCESSED = "item_last_accessed";
        public static final String ITEM_CALORIE_COL = "item_cal";
        public static final String ITEM_PROTEIN_COL = "item_protein";
        public static final String ITEM_FAT_COL = "item_fat";
        public static final String ITEM_CARBS_COL = "item_carbs";
        public static final String ITEM_FIBER_COL = "item_fiber";
        public static final String ITEM_SATFAT_COL = "item_satfat";
        public static final String ITEM_MONOFAT_COL = "item_monofat";
        public static final String ITEM_POLYFAT_COL = "item_polyfat";
        public static final String ITEM_CHOLESTEROL_COL = "item_cholesterol";


        public static Uri buildFoodUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFoodWithNDBNO(String ndbno) {
            return CONTENT_URI.buildUpon().appendPath(ndbno).build();
        }

        public static String getNDBNOFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }


}
