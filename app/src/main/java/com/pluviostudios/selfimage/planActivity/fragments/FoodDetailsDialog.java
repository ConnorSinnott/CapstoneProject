package com.pluviostudios.selfimage.planActivity.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.DatabaseContract;
import com.pluviostudios.selfimage.planActivity.data.FoodItemDBHandler;
import com.pluviostudios.selfimage.utilities.MissingExtraException;
import com.pluviostudios.usdanutritionalapi.FoodItem;

/**
 * Created by Spectre on 6/7/2016.
 */
public class FoodDetailsDialog extends DialogFragment {

    public static final String REFERENCE_ID = "AddQuantityDialogFragment";

    public static final String EXTRA_FOOD_ITEM = "foodItem";

    public static final int CATEGORY_REQUEST_CODE = 1;

    private View mRoot;
    private TextView textName;
    private Button buttonAdd, buttonSubtract, buttonConfirm;
    private Button buttonCategory;
    private EditText editTextQuantity;

    private TextView textCalories;
    private TextView textProtein;
    private TextView textFat;
    private TextView textCarbs;
    private TextView textFiber;
    private TextView textSatFat;
    private TextView textMonoFat;
    private TextView textPolyFat;
    private TextView textCholesterol;

    private FoodItem mFoodItem;

    private OnDialogQuantityConfirm onQuantityDialogConfirm;
    private int mQuantity = 1;
    private int mCategory = 0;

    public static FoodDetailsDialog buildFoodDetailsDialog(FoodItem foodData, OnDialogQuantityConfirm mOnConfirm) {
        FoodDetailsDialog newFragment = new FoodDetailsDialog();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_FOOD_ITEM, foodData);
        newFragment.setArguments(args);
        newFragment.setOnQuantityDialogConfirm(mOnConfirm);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (!args.containsKey(EXTRA_FOOD_ITEM)) {
            throw new MissingExtraException(EXTRA_FOOD_ITEM);
        }

        mRoot = inflater.inflate(R.layout.dialog_add_quantity, container, false);
        init();

        mFoodItem = (FoodItem) args.getSerializable(EXTRA_FOOD_ITEM);

        textName.setText(args.getString(DatabaseContract.FoodEntry.ITEM_NAME_COL));
        updateQuantityUI(mQuantity);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantityUI(++mQuantity);
            }
        });
        buttonSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantityUI(--mQuantity);
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onQuantityDialogConfirm != null) {
                    onQuantityDialogConfirm.onDialogQuantityConfirm(mFoodItem, mCategory, mQuantity);
                    dismiss();
                }
            }
        });

        buttonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentCategoryPicker fragmentCategoryPicker =
                        DialogFragmentCategoryPicker.createCategoryDialog(
                                FoodDetailsDialog.this,
                                CATEGORY_REQUEST_CODE);
                fragmentCategoryPicker.show(getFragmentManager(), DialogFragmentCategoryPicker.REFERENCE_ID);
            }
        });

        if (!mFoodItem.hasNutrientData()) {

            FoodItem.OnDataPulled onDataPulled = new FoodItem.OnDataPulled() {
                @Override
                public void OnDataPulled(FoodItem foodItem) {
                    populateData();
                }
            };

            if (FoodItemDBHandler.hasFoodInDatabase(getContext(), mFoodItem)) {
                FoodItemDBHandler.pullNutritionalArrayFromDatabase(getContext(), mFoodItem, onDataPulled);
            } else {
                mFoodItem.pullNutrientData(getContext(), onDataPulled);
            }

        } else {
            populateData();
        }

        updateCategory();

        return mRoot;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CATEGORY_REQUEST_CODE) {
            mCategory = data.getExtras().getInt(DialogFragmentCategoryPicker.EXTRA_CATEGORY);
            updateCategory();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        editTextQuantity = (EditText) mRoot.findViewById(R.id.dialog_food_details_count);

        buttonAdd = (Button) mRoot.findViewById(R.id.dialog_food_details_add);
        buttonSubtract = (Button) mRoot.findViewById(R.id.dialog_food_details_subtract);
        buttonCategory = (Button) mRoot.findViewById(R.id.dialog_food_details_category);

        textName = (TextView) mRoot.findViewById(R.id.dialog_food_details_name);
        textCalories = (TextView) mRoot.findViewById(R.id.dialog_food_details_calories);
        textProtein = (TextView) mRoot.findViewById(R.id.dialog_food_details_protein);
        textFat = (TextView) mRoot.findViewById(R.id.dialog_food_details_fat);
        textCarbs = (TextView) mRoot.findViewById(R.id.dialog_food_details_carbs);
        textFiber = (TextView) mRoot.findViewById(R.id.dialog_food_details_fiber);
        textSatFat = (TextView) mRoot.findViewById(R.id.dialog_food_details_satfat);
        textMonoFat = (TextView) mRoot.findViewById(R.id.dialog_food_details_monofat);
        textPolyFat = (TextView) mRoot.findViewById(R.id.dialog_food_details_polyfat);
        textCholesterol = (TextView) mRoot.findViewById(R.id.dialog_food_details_cholesterol);

        buttonConfirm = (Button) mRoot.findViewById(R.id.dialog_food_details_confirm);
    }

    private void populateData() {
        textCalories.setText(String.valueOf(mFoodItem.getNutrientData().get(FoodItem.Calories) * mQuantity));
        textProtein.setText(String.valueOf(mFoodItem.getNutrientData().get(FoodItem.Protein) * mQuantity));
        textFat.setText(String.valueOf(mFoodItem.getNutrientData().get(FoodItem.Fat) * mQuantity));
        textCarbs.setText(String.valueOf(mFoodItem.getNutrientData().get(FoodItem.Carbs) * mQuantity));
        textFiber.setText(String.valueOf(mFoodItem.getNutrientData().get(FoodItem.Fiber) * mQuantity));
        textSatFat.setText(String.valueOf(mFoodItem.getNutrientData().get(FoodItem.SatFat) * mQuantity));
        textMonoFat.setText(String.valueOf(mFoodItem.getNutrientData().get(FoodItem.MonoFat) * mQuantity));
        textPolyFat.setText(String.valueOf(mFoodItem.getNutrientData().get(FoodItem.PolyFat) * mQuantity));
        textCholesterol.setText(String.valueOf(mFoodItem.getNutrientData().get(FoodItem.Cholesterol) * mQuantity));
    }

    private void updateQuantityUI(int quantity) {
        editTextQuantity.setText(String.valueOf(quantity));
        if (mFoodItem.hasNutrientData()) {
            populateData();
        }
    }

    public interface OnDialogQuantityConfirm {
        void onDialogQuantityConfirm(FoodItem foodItem, int category, int quantity);
    }

    public void setOnQuantityDialogConfirm(OnDialogQuantityConfirm onQuantityDialogConfirm) {
        this.onQuantityDialogConfirm = onQuantityDialogConfirm;
    }

    private void updateCategory() {
        Cursor c = getContext().getContentResolver().query(
                DatabaseContract.CategoryEntry.buildCategoryWithIndex(mCategory),
                new String[]{DatabaseContract.CategoryEntry.CATEGORY_NAME_COL},
                null,
                null,
                null);
        if (c != null && c.moveToFirst()) {
            buttonCategory.setText(c.getString(0));
            c.close();
        }
    }

}
