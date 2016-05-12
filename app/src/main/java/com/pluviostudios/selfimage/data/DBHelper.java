package com.pluviostudios.selfimage.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.orhanobut.logger.Logger;

/**
 * Created by Spectre on 5/11/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SelfImage.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Logger.init();
        Logger.i("ON CREATE!");

        final String CreateDateTable = "CREATE TABLE " + DatabaseContract.DateEntry.TABLE_NAME + " ("
                + DatabaseContract.DateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.DateEntry.DATE_COL + " LONG NOT NULL,"
                + DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL + " TEXT NOT NULL, "
                + " UNIQUE (" + DatabaseContract.DateEntry.DATE_COL + ") ON CONFLICT REPLACE);";

        final String CreateDiaryTable = "CREATE TABLE " + DatabaseContract.DiaryEntry.TABLE_NAME + " ("
                + DatabaseContract.DiaryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.DiaryEntry.DATE_KEY_COL + " INTEGER NOT NULL, "
                + DatabaseContract.DiaryEntry.ITEM_NAME_COL + " TEXT NOT NULL ,"
                + DatabaseContract.DiaryEntry.ITEM_CAL_COL + " INTEGER NOT NULL, "
                + " FOREIGN KEY (" + DatabaseContract.DiaryEntry.DATE_KEY_COL + ") REFERENCES "
                + DatabaseContract.DateEntry.TABLE_NAME + " (" + DatabaseContract.DateEntry._ID + "), "
                + " UNIQUE (" + DatabaseContract.DiaryEntry.DATE_KEY_COL + ") ON CONFLICT REPLACE);";

        db.execSQL(CreateDateTable);
        db.execSQL(CreateDiaryTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DateEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DiaryEntry.TABLE_NAME);
    }

}
