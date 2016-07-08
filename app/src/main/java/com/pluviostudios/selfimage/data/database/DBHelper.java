package com.pluviostudios.selfimage.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.dataContainers.date.DateItem;
import com.pluviostudios.selfimage.data.dataContainers.diary.DiaryItem;
import com.pluviostudios.selfimage.data.dataContainers.food.FoodItemWithDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

        String imageFileName = "SelfImg_Debug";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.fitstick);
        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(image);
            bm.compress(Bitmap.CompressFormat.JPEG, 10, fos);
            fos.close();
        } catch (IOException e) {
            Log.e("app", e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        Uri storageUri = Uri.fromFile(image);

        DateItem dateItem = new DateItem(1467010800000L, storageUri.toString());
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
        foodItemMilk.setNutrientData(new ArrayList<Double>() {{
            add(43.0);
            add(3.48);
            add(0.97);
            add(4.97);
            add(0.0);
            add(0.604);
            add(0.28);
            add(0.036);
            add(4.0);
        }});
        FoodItemWithDB foodItemBagel = new FoodItemWithDB("Bagels, egg", "18003");
        foodItemBagel.setNutrientData(new ArrayList<Double>() {{
            add(278.0);
            add(10.6);
            add(2.1);
            add(53.0);
            add(2.3);
            add(0.421);
            add(0.42);
            add(0.642);
            add(24.0);
        }});
        foodItemEgg.save(context);
        foodItemMilk.save(context);
        foodItemBagel.save(context);

        new DiaryItem(foodItemEgg, dateItem.date, 0, 2).save(context);
        new DiaryItem(foodItemMilk, dateItem.date, 0, 1).save(context);
        new DiaryItem(foodItemBagel, dateItem.date, 0, 1).save(context);

        FoodItemWithDB foodItemSub = new FoodItemWithDB("SUBWAY, SUBWAY CLUB sub on white bread with lettuce and tomato", "21152");
        foodItemSub.setNutrientData(new ArrayList<Double>() {{
            add(146.0);
            add(10.66);
            add(2.42);
            add(20.36);
            add(1.4);
            add(0.57);
            add(0.615);
            add(0.844);
            add(16.0);
        }});
        FoodItemWithDB foodItemSprite = new FoodItemWithDB("Beverages, carbonated, SPRITE, lemon-lime, without caffeine", "14145");
        foodItemSprite.setNutrientData(new ArrayList<Double>() {{
            add(40.0);
            add(0.05);
            add(0.02);
            add(10.14);
            add(0.0);
            add(0.0);
            add(0.0);
            add(0.0);
            add(0.0);
        }});
        foodItemSprite.save(context);
        foodItemSprite.save(context);

        new DiaryItem(foodItemSub, dateItem.date, 2, 1).save(context);
        new DiaryItem(foodItemSprite, dateItem.date, 2, 1).save(context);

        FoodItemWithDB foodItemPretzel = new FoodItemWithDB("Pretzels, soft", "43109");
        foodItemPretzel.setNutrientData(new ArrayList<Double>() {{
            add(338.0);
            add(8.2);
            add(3.1);
            add(69.39);
            add(1.7);
            add(0.695);
            add(1.071);
            add(0.948);
            add(0.0);
        }});
        foodItemPretzel.save(context);

        new DiaryItem(foodItemPretzel, dateItem.date, 3, 2).save(context);

        FoodItemWithDB foodItemBeef = new FoodItemWithDB("Beef, chuck, under blade pot roast, boneless, separable lean only, trimmed to 0\" fat, all grades, cooked, braised", "13285");
        foodItemBeef.setNutrientData(new ArrayList<Double>() {{
            add(216.0);
            add(30.68);
            add(9.44);
            add(0.0);
            add(0.0);
            add(3.487);
            add(4.164);
            add(0.52);
            add(102.0);
        }});
        foodItemBeef.save(context);

        new DiaryItem(foodItemBeef, dateItem.date, 4, 2).save(context);

        FoodItemWithDB foodItemIceCream = new FoodItemWithDB("Ice cream sandwich, made with light ice cream, vanilla", "01241");
        foodItemIceCream.setNutrientData(new ArrayList<Double>() {{
            add(186.0);
            add(4.29);
            add(3.04);
            add(39.64);
            add(0.0);
            add(0.821);
            add(0.942);
            add(0.805);
            add(7.0);
        }});
        foodItemIceCream.save(context);

        new DiaryItem(foodItemIceCream, dateItem.date, 5, 1).save(context);

    }

}

