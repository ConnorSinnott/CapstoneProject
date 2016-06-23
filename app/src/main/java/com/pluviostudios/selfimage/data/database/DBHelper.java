package com.pluviostudios.selfimage.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Spectre on 5/11/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SelfImage.db";
    private static final int DATABASE_VERSION = 4;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CreateDateTable = "CREATE TABLE " + DatabaseContract.DateEntry.TABLE_NAME + " ("
                + DatabaseContract.DateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.DateEntry.DATE_COL + " LONG NOT NULL,"
                + DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL + " TEXT, "
                + " UNIQUE (" + DatabaseContract.DateEntry.DATE_COL + ") ON CONFLICT REPLACE)";

        final String CreateDiaryTable = "CREATE TABLE " + DatabaseContract.DiaryEntry.TABLE_NAME + " ("
                + DatabaseContract.DiaryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.DiaryEntry.ITEM_DATE_COL + " LONG NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_NDBNO_COL + " TEXT NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL + " INTEGER NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL + " INTEGER NOT NULL, "
                + " FOREIGN KEY (" + DatabaseContract.DiaryEntry.ITEM_DATE_COL + ") REFERENCES "
                + DatabaseContract.DateEntry.TABLE_NAME + " (" + DatabaseContract.DateEntry.DATE_COL + ")"
                + " FOREIGN KEY (" + DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL + ") REFERENCES "
                + DatabaseContract.CategoryEntry.TABLE_NAME + "(" + DatabaseContract.CategoryEntry._ID + ")"
                + " FOREIGN KEY (" + DatabaseContract.DiaryEntry.ITEM_NDBNO_COL + ") REFERENCES "
                + DatabaseContract.FoodEntry.TABLE_NAME + "(" + DatabaseContract.FoodEntry.ITEM_NDBNO_COL + ")"
                + ")";

        final String CreateFoodTable = "CREATE TABLE " + DatabaseContract.FoodEntry.TABLE_NAME + " ("
                + DatabaseContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.FoodEntry.ITEM_NDBNO_COL + " TEXT NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_NAME_COL + " TEXT NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_LAST_ACCESSED + " LONG NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_CALORIE_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_PROTEIN_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_FAT_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_CARBS_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_FIBER_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_SATFAT_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_MONOFAT_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_POLYFAT_COL + " REAL NOT NULL, "
                + DatabaseContract.FoodEntry.ITEM_CHOLESTEROL_COL + " REAL NOT NULL, "
                + " UNIQUE (" + DatabaseContract.FoodEntry.ITEM_NDBNO_COL + ") ON CONFLICT ABORT) ";

        final String CreateCategoryTable = "CREATE TABLE " + DatabaseContract.CategoryEntry.TABLE_NAME + " ("
                + DatabaseContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.CategoryEntry.CATEGORY_INDEX_COL + " INTEGER UNIQUE NOT NULL, "
                + DatabaseContract.CategoryEntry.CATEGORY_NAME_COL + " TEXT UNIQUE NOT NULL, "
                + " UNIQUE (" + DatabaseContract.CategoryEntry.CATEGORY_NAME_COL + " ) ON CONFLICT ABORT)";

        db.execSQL(CreateDateTable);
        db.execSQL(CreateDiaryTable);
        db.execSQL(CreateFoodTable);
        db.execSQL(CreateCategoryTable);

        String[] cats = new String[]{
                "Breakfast",
                "Pre-Lunch Snack",
                "Lunch",
                "Post-Lunch Snack",
                "Dinner",
                "Dessert"
        };

        for (int i = 0; i < cats.length; i++) {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.CategoryEntry.CATEGORY_INDEX_COL, i);
            values.put(DatabaseContract.CategoryEntry.CATEGORY_NAME_COL, cats[i]);
            db.insert(DatabaseContract.CategoryEntry.TABLE_NAME, null, values);
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DateEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DiaryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.FoodEntry.TABLE_NAME);
    }

}

