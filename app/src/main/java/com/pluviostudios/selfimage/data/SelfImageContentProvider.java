package com.pluviostudios.selfimage.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Spectre on 5/11/2016.
 */
public class SelfImageContentProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DBHelper mOpenHelper;

    static final int DATE = 100;
    static final int DATE_WITH_DATE = 101;

    static final int CATEGORY = 300;

    static final int DIARY = 200;
    static final int DIARY_WITH_DATE = 201;
    static final int DIARY_WITH_DATE_AND_CATEGORY = 202;
    static final int DIARY_WITH_DATE_AND_CATEGORY_AND_NDBNO = 203;

    private static final SQLiteQueryBuilder sMealsByDateQueryBuilder;

    static {
        sMealsByDateQueryBuilder = new SQLiteQueryBuilder();
        sMealsByDateQueryBuilder.setTables(
                DatabaseContract.DiaryEntry.TABLE_NAME + " INNER JOIN " +
                        DatabaseContract.DateEntry.TABLE_NAME +
                        " ON " + DatabaseContract.DiaryEntry.TABLE_NAME +
                        "." + DatabaseContract.DiaryEntry.DATE_COL +
                        " = " + DatabaseContract.DateEntry.TABLE_NAME +
                        "." + DatabaseContract.DateEntry.DATE_COL
                        + " INNER JOIN " +
                        DatabaseContract.CategoryEntry.TABLE_NAME +
                        " ON " + DatabaseContract.DiaryEntry.TABLE_NAME +
                        "." + DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL +
                        " = " + DatabaseContract.CategoryEntry.TABLE_NAME +
                        "." + DatabaseContract.CategoryEntry._ID
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DatabaseContract.PATH_DATE, DATE);
        matcher.addURI(authority, DatabaseContract.PATH_DATE + "/*", DATE_WITH_DATE);

        matcher.addURI(authority, DatabaseContract.PATH_DIARY, DIARY);
        matcher.addURI(authority, DatabaseContract.PATH_DIARY + "/*", DIARY_WITH_DATE);
        matcher.addURI(authority, DatabaseContract.PATH_DIARY + "/*/*", DIARY_WITH_DATE_AND_CATEGORY);
        matcher.addURI(authority, DatabaseContract.PATH_DIARY + "/*/*/*", DIARY_WITH_DATE_AND_CATEGORY_AND_NDBNO);

        matcher.addURI(authority, DatabaseContract.PATH_CATEGORY, CATEGORY);

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
                return DatabaseContract.DiaryEntry.CONTENT_TYPE;
            case DIARY_WITH_DATE:
                return DatabaseContract.DiaryEntry.CONTENT_TYPE;
            case DIARY_WITH_DATE_AND_CATEGORY:
                return DatabaseContract.DiaryEntry.CONTENT_TYPE;
            case DIARY_WITH_DATE_AND_CATEGORY_AND_NDBNO:
                return DatabaseContract.DiaryEntry.CONTENT_ITEM_TYPE;
            case CATEGORY:
                return DatabaseContract.CategoryEntry.CONTENT_TYPE;
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

                long startDate = DatabaseContract.DateEntry.getStartDateFromUri(uri);

                selection = createSelection(selection,
                        DatabaseContract.DateEntry.TABLE_NAME + "." + DatabaseContract.DateEntry.DATE_COL);

                selectionArgs = createSelectionArgs(selectionArgs,
                        String.valueOf(startDate));

                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.DateEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
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

                selection = createSelection(selection,
                        DatabaseContract.DateEntry.TABLE_NAME + "." + DatabaseContract.DateEntry.DATE_COL);

                selectionArgs = createSelectionArgs(selectionArgs,
                        String.valueOf(startDate));

                retCursor = sMealsByDateQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                break;
            }
            case DIARY_WITH_DATE_AND_CATEGORY: {

                long startDate = DatabaseContract.DiaryEntry.getStartDateFromUri(uri);
                int category = DatabaseContract.DiaryEntry.getCategoryFromUri(uri);

                selection = createSelection(selection,
                        DatabaseContract.DateEntry.TABLE_NAME + "." + DatabaseContract.DateEntry.DATE_COL,
                        DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL);

                selectionArgs = createSelectionArgs(selectionArgs,
                        String.valueOf(startDate),
                        String.valueOf(category));

                retCursor = sMealsByDateQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            }
            case DIARY_WITH_DATE_AND_CATEGORY_AND_NDBNO: {

                long startDate = DatabaseContract.DiaryEntry.getStartDateFromUri(uri);
                int category = DatabaseContract.DiaryEntry.getCategoryFromUri(uri);
                String ndbno = DatabaseContract.DiaryEntry.getNDBNOFromUri(uri);

                selection = createSelection(selection,
                        DatabaseContract.DateEntry.TABLE_NAME + "." + DatabaseContract.DateEntry.DATE_COL,
                        DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL,
                        DatabaseContract.DiaryEntry.ITEM_NDBNO_COL);

                selectionArgs = createSelectionArgs(selectionArgs,
                        String.valueOf(startDate),
                        String.valueOf(category),
                        ndbno);

                retCursor = sMealsByDateQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
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
                    throw new android.database.SQLException("Failed to insert row int " + uri);
                break;
            }
            case DIARY: {
                long _id = db.insert(DatabaseContract.DiaryEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DatabaseContract.DiaryEntry.buildDiaryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row int " + uri);
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
            case DIARY_WITH_DATE_AND_CATEGORY_AND_NDBNO: {

                long startDate = DatabaseContract.DiaryEntry.getStartDateFromUri(uri);
                int category = DatabaseContract.DiaryEntry.getCategoryFromUri(uri);
                String ndbno = DatabaseContract.DiaryEntry.getNDBNOFromUri(uri);

                selection = createSelection(selection,
                        DatabaseContract.DateEntry.TABLE_NAME + "." + DatabaseContract.DateEntry.DATE_COL,
                        DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL,
                        DatabaseContract.DiaryEntry.ITEM_NDBNO_COL);

                selectionArgs = createSelectionArgs(selectionArgs,
                        String.valueOf(startDate),
                        String.valueOf(category),
                        ndbno);

                rowsDeleted = db.delete(DatabaseContract.DiaryEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

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
            case DIARY_WITH_DATE_AND_CATEGORY_AND_NDBNO: {

                long startDate = DatabaseContract.DiaryEntry.getStartDateFromUri(uri);
                int category = DatabaseContract.DiaryEntry.getCategoryFromUri(uri);
                String ndbno = DatabaseContract.DiaryEntry.getNDBNOFromUri(uri);

                selection = createSelection(selection,
                        DatabaseContract.DateEntry.TABLE_NAME + "." + DatabaseContract.DateEntry.DATE_COL,
                        DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL,
                        DatabaseContract.DiaryEntry.ITEM_NDBNO_COL);

                selectionArgs = createSelectionArgs(selectionArgs,
                        String.valueOf(startDate),
                        String.valueOf(category),
                        ndbno);

                rowsUpdated = db.update(DatabaseContract.DiaryEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

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

    private String createSelection(String passedSelection, String... additionalSelectionColumNames) {
        if (passedSelection != null && additionalSelectionColumNames != null) {

            for (String x : additionalSelectionColumNames) {
                passedSelection += " AND " + x + " = ? ";
            }
        }

        return passedSelection;
    }

    private String[] createSelectionArgs(String[] passedSelectionArgs, String... additionalArgs) {
        if (passedSelectionArgs != null && additionalArgs != null) {

            String[] newSelection = new String[passedSelectionArgs.length + additionalArgs.length];
            System.arraycopy(passedSelectionArgs, 0, newSelection, 0, passedSelectionArgs.length);
            System.arraycopy(additionalArgs, 0, newSelection, passedSelectionArgs.length, additionalArgs.length);
            return newSelection;

        } else return passedSelectionArgs;
    }

}

