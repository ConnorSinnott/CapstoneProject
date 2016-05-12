package com.pluviostudios.selfimage.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

/**
 * Created by Spectre on 5/11/2016.
 */
public class SelfImageContentProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DBHelper mOpenHelper;

    static final int DATE = 100;
    static final int DATE_WITH_DATE = 101;
    static final int DIARY = 200;
    static final int DIARY_WITH_DATE = 201;

    private static final SQLiteQueryBuilder sMealsByDateQueryBuilder;

    static {
        sMealsByDateQueryBuilder = new SQLiteQueryBuilder();
        sMealsByDateQueryBuilder.setTables(
                DatabaseContract.DiaryEntry.TABLE_NAME + " INNER JOIN " +
                        DatabaseContract.DateEntry.TABLE_NAME +
                        " ON " + DatabaseContract.DiaryEntry.TABLE_NAME +
                        "." + DatabaseContract.DiaryEntry.DATE_KEY_COL +
                        " = " + DatabaseContract.DateEntry.TABLE_NAME +
                        "." + DatabaseContract.DateEntry._ID);
    }


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DatabaseContract.PATH_DATE, DATE);
        matcher.addURI(authority, DatabaseContract.PATH_DATE + "/#", DATE_WITH_DATE);

        matcher.addURI(authority, DatabaseContract.PATH_DIARY, DIARY);
        matcher.addURI(authority, DatabaseContract.PATH_DIARY + "/#", DIARY_WITH_DATE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case DATE:
                return DatabaseContract.DateEntry.CONTENT_TYPE;
            case DATE_WITH_DATE:
                return DatabaseContract.DateEntry.CONTENT_ITEM_TYPE;
            case DIARY:
                return DatabaseContract.DateEntry.CONTENT_TYPE;
            case DIARY_WITH_DATE:
                return DatabaseContract.DiaryEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case DATE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.DateEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case DATE_WITH_DATE: {

                Logger.init();
                Logger.i(uri.toString());

                long startDate = DatabaseContract.DateEntry.getStartDateFromUri(uri);

                String dateSettingSelection =
                        DatabaseContract.DateEntry.TABLE_NAME +
                                "." + DatabaseContract.DateEntry.DATE_COL + " = ? ";
                String[] selections = {String.valueOf(startDate)};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.DateEntry.TABLE_NAME,
                        projection,
                        dateSettingSelection,
                        selections,
                        null,
                        null,
                        null
                );
                break;
            }
            case DIARY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.DiaryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case DIARY_WITH_DATE: {
                long startDate = DatabaseContract.DiaryEntry.getStartDateFromUri(uri);

                String dateSettingSelection =
                        DatabaseContract.DateEntry.TABLE_NAME +
                                "." + DatabaseContract.DateEntry.DATE_COL + " = ? ";
                String[] selections = {String.valueOf(startDate)};

                retCursor = sMealsByDateQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        dateSettingSelection,
                        selections,
                        null,
                        null,
                        null
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case DATE: {
                long _id = db.insert(DatabaseContract.DateEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DatabaseContract.DateEntry.buildDateUri(_id);
                else
                    throw new android.database.SQLException("FAiled to insert row int " + uri);
                break;
            }
            case DIARY: {
                long _id = db.insert(DatabaseContract.DiaryEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DatabaseContract.DiaryEntry.buildDiaryUri(_id);
                else
                    throw new android.database.SQLException("FAiled to insert row int " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (null == selection) selection = "1";
        switch (match) {
            case DATE: {
                rowsDeleted = db.delete(
                        DatabaseContract.DateEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case DIARY: {
                rowsDeleted = db.delete(
                        DatabaseContract.DiaryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case DATE: {
                rowsUpdated = db.update(DatabaseContract.DateEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case DIARY: {
                rowsUpdated = db.update(DatabaseContract.DateEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
