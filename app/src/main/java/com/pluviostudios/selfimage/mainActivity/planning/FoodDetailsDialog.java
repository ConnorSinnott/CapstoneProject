package com.pluviostudios.selfimage.mainActivity.planning;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.dataContainers.diary.DiaryItem;
import com.pluviostudios.selfimage.data.dataContainers.food.FoodItemWithDB;
import com.pluviostudios.selfimage.utilities.DateUtils;
import com.pluviostudios.selfimage.utilities.MissingExtraException;
import com.pluviostudios.usdanutritionalapi.FoodItem;

/**
 * Created by Spectre on 6/7/2016.
 */
public class FoodDetailsDialog extends DialogFragment {

    public static final String REFERENCE_ID = "AddQuantityDialogFragment";

    public static final String EXTRA_FOOD_ITEM = "extra_foodItem";
    public static final String EXTRA_DIARY_ITEM = "extra_diaryItem";
    public static final String EXTRA_DATE = "extra_date";

    public static final int CATEGORY_REQUEST_CODE = 1;

    private View mRoot;
    private ViewFlipper mViewFlipper;
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

    private OnDetailsDialogConfirm mOnDetailsDialogConfirm;

    private DiaryItem mDiaryItem;
    private long mDate;
    private int mQuantity = 1;
    private int mCategory = 0;

    public static FoodDetailsDialog buildFoodDetailsDialog(FoodItemWithDB foodItem, OnDetailsDialogConfirm mOnConfirm) {
        FoodDetailsDialog newFragment = new FoodDetailsDialog();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_FOOD_ITEM, foodItem);
        args.putLong(EXTRA_DATE, DateUtils.getCurrentNormalizedDate());
        newFragment.setArguments(args);
        newFragment.setOnDetailsDialogConfirm(mOnConfirm);
        return newFragment;
    }

    public static FoodDetailsDialog buildFoodDetailsDialog(DiaryItem diaryItem, OnDetailsDialogConfirm mOnConfirm) {
        FoodDetailsDialog newFragment = new FoodDetailsDialog();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DIARY_ITEM, diaryItem);
        newFragment.setArguments(args);
        newFragment.setOnDetailsDialogConfirm(mOnConfirm);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();

        if (args.containsKey(EXTRA_FOOD_ITEM)) {
            // If a FoodItemWithDB has been sent create the Diary Object
            if (!args.containsKey(EXTRA_DATE)) {
                throw new MissingExtraException(EXTRA_DATE);
            }
            FoodItemWithDB foodItem = (FoodItemWithDB) args.getSerializable(EXTRA_FOOD_ITEM);
            mDate = args.getLong(EXTRA_DATE);
            mDiaryItem = new DiaryItem(foodItem, mDate, mCategory, mQuantity);

        } else if (args.containsKey(EXTRA_DIARY_ITEM)) {
            // If a DiaryItem has been sent
            mDiaryItem = (DiaryItem) args.getSerializable(EXTRA_DIARY_ITEM);
            mDate = mDiaryItem.date;
            mCategory = mDiaryItem.category;
            mQuantity = mDiaryItem.quantity;

        } else {
            throw new MissingExtraException(EXTRA_FOOD_ITEM + " and " + EXTRA_DATE + " or " + EXTRA_DIARY_ITEM);
        }

        mRoot = inflater.inflate(R.layout.dialog_food_details, container, false);
        init();

        // Set Name
        textName.setText(mDiaryItem.foodItem.getFoodName());

        // Set Quantity
        updateQuantityUI(mQuantity);

        // Set Category
        updateCategory();

        // Initialize Add and Subtract
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

        // Initialize Category List
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

        // Initialize Confirm
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDetailsDialogConfirm != null) {
                    mOnDetailsDialogConfirm.onDetailsDialogConfirm(mDiaryItem);
                    dismiss();
                }
            }
        });

        // Pull nutrient data if is has yet to be loaded. Then Display
        mDiaryItem.foodItem.getNutrientDataWithDB(getContext(), new FoodItemWithDB.OnDataPulledWithDB() {
            @Override
            public void onDataPulled(FoodItemWithDB foodItemWithDB) {
                populateData();
                mViewFlipper.showNext();
            }
        });

        return mRoot;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Receive the activity result from the category picker
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

        mViewFlipper = (ViewFlipper) mRoot.findViewById(R.id.dialog_food_details_view_flipper);

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
        textCalories.setText(String.valueOf(mDiaryItem.foodItem.getNutrientData().get(FoodItem.Calories) * mQuantity));
        textProtein.setText(String.valueOf(mDiaryItem.foodItem.getNutrientData().get(FoodItem.Protein) * mQuantity));
        textFat.setText(String.valueOf(mDiaryItem.foodItem.getNutrientData().get(FoodItem.Fat) * mQuantity));
        textCarbs.setText(String.valueOf(mDiaryItem.foodItem.getNutrientData().get(FoodItem.Carbs) * mQuantity));
        textFiber.setText(String.valueOf(mDiaryItem.foodItem.getNutrientData().get(FoodItem.Fiber) * mQuantity));
        textSatFat.setText(String.valueOf(mDiaryItem.foodItem.getNutrientData().get(FoodItem.SatFat) * mQuantity));
        textMonoFat.setText(String.valueOf(mDiaryItem.foodItem.getNutrientData().get(FoodItem.MonoFat) * mQuantity));
        textPolyFat.setText(String.valueOf(mDiaryItem.foodItem.getNutrientData().get(FoodItem.PolyFat) * mQuantity));
        textCholesterol.setText(String.valueOf(mDiaryItem.foodItem.getNutrientData().get(FoodItem.Cholesterol) * mQuantity));
    }

    private void updateQuantityUI(int quantity) {
        mDiaryItem.quantity = quantity;
        editTextQuantity.setText(String.valueOf(mDiaryItem.quantity));
        if (mDiaryItem.foodItem.hasNutrientData()) {
            populateData();
        }
    }

    private void updateCategory() {
        mDiaryItem.category = mCategory;
        buttonCategory.setText(mDiaryItem.getCategoryName(getContext()));
    }

    public void setOnDetailsDialogConfirm(OnDetailsDialogConfirm onDetailsDialogConfirm) {
        mOnDetailsDialogConfirm = onDetailsDialogConfirm;
    }

    /**
     * Created by Spectre on 6/22/2016.
     */
    public interface OnDetailsDialogConfirm {

        void onDetailsDialogConfirm(DiaryItem diaryItem);

    }
}
