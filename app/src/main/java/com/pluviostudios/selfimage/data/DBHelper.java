package com.pluviostudios.selfimage.data;

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
                + DatabaseContract.DiaryEntry.DATE_COL + " LONG NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_NAME_COL + " TEXT NOT NULL ,"
                + DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL + " INTEGER NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_NDBNO_COL + " TEXT NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL + " INTEGER NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_CALORIE_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_PROTEIN_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_FAT_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_CARBS_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_FIBER_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_SATFAT_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_MONOFAT_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_POLYFAT_COL + " REAL NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_CHOLESTEROL_COL + " REAL NOT NULL, "
                + " FOREIGN KEY (" + DatabaseContract.DiaryEntry.DATE_COL + ") REFERENCES "
                + DatabaseContract.DateEntry.TABLE_NAME + " (" + DatabaseContract.DateEntry.DATE_COL + ")"
                + " FOREIGN KEY (" + DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL + ") REFERENCES "
                + DatabaseContract.CategoryEntry.TABLE_NAME + "(" + DatabaseContract.CategoryEntry._ID + ")"
                + ")";

        final String CreateCategoryTable = "CREATE TABLE " + DatabaseContract.CategoryEntry.TABLE_NAME + " ("
                + DatabaseContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.CategoryEntry.CATEGORY_INDEX_COL + " INTEGER UNIQUE NOT NULL, "
                + DatabaseContract.CategoryEntry.CATEGORY_NAME_COL + " TEXT UNIQUE NOT NULL, "
                + " UNIQUE (" + DatabaseContract.CategoryEntry.CATEGORY_NAME_COL + " ) ON CONFLICT ABORT)";

        db.execSQL(CreateDateTable);
        db.execSQL(CreateDiaryTable);
        db.execSQL(CreateCategoryTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DateEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DiaryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.CategoryEntry.TABLE_NAME);
    }

}

