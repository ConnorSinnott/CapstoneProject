package com.pluviostudios.selfimage.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pluviostudios.selfimage.data.dataContainers.date.DateItem;
import com.pluviostudios.selfimage.data.dataContainers.diary.DiaryItem;
import com.pluviostudios.selfimage.data.dataContainers.food.FoodItemWithDB;

import java.util.ArrayList;

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

    public static void addDummyData(Context context) {

        DateItem dateItem = new DateItem(1467010800000L, null);
        dateItem.save(context);

        FoodItemWithDB foodItemEgg = new FoodItemWithDB("Egg, whole, cooked, fried", "01128");
        foodItemEgg.setNutrientData(new ArrayList<Double>() {{
            add(196.0);
            add(13.61);
            add(14.84);
            add(0.83);
            add(0.0);
            add(4.323);
            add(6.182);
            add(3.251);
            add(401.0);
        }});
        FoodItemWithDB foodItemMilk = new FoodItemWithDB("Milk, lowfat, fluid, 1% milkfat, with added nonfat milk solids, vitamin A and vitamin D", "01083");
        FoodItemWithDB foodItemBagel = new FoodItemWithDB("Bagels, egg", "18003");

        foodItemEgg.save(context);
        foodItemMilk.save(context);
        foodItemBagel.save(context);

        new DiaryItem(foodItemEgg, dateItem.date, 0, 2).save(context);
        new DiaryItem(foodItemMilk, dateItem.date, 0, 1).save(context);
        new DiaryItem(foodItemBagel, dateItem.date, 0, 1).save(context);

        FoodItemWithDB foodItemSub = new FoodItemWithDB("SUBWAY, SUBWAY CLUB sub on white bread with lettuce and tomato", "21152");
        FoodItemWithDB foodItemSprite = new FoodItemWithDB("Beverages, carbonated, SPRITE, lemon-lime, without caffeine", "14145");

        foodItemSprite.save(context);
        foodItemSprite.save(context);

        new DiaryItem(foodItemSub, dateItem.date, 2, 1).save(context);
        new DiaryItem(foodItemSprite, dateItem.date, 2, 1).save(context);

        FoodItemWithDB foodItemPretzel = new FoodItemWithDB("Pretzels, soft", "43109");
        foodItemPretzel.save(context);

        new DiaryItem(foodItemPretzel, dateItem.date, 3, 2).save(context);

        FoodItemWithDB foodItemBeef = new FoodItemWithDB("Beef, chuck, under blade pot roast, boneless, separable lean only, trimmed to 0\" fat, all grades, cooked, braised", "13285");
        foodItemBeef.save(context);

        new DiaryItem(foodItemBeef, dateItem.date, 4, 2).save(context);

        FoodItemWithDB foodItemIceCream = new FoodItemWithDB("Ice cream sandwich, made with light ice cream, vanilla", "01241");
        foodItemIceCream.save(context);

        new DiaryItem(foodItemIceCream, dateItem.date, 5, 1).save(context);

    }

}

