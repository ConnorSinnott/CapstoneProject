package com.pluviostudios.selfimage.planActivity.fragments.planning;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.DatabaseContract;
import com.pluviostudios.selfimage.planActivity.MealPlanningActivity;
import com.pluviostudios.selfimage.planActivity.data.OnFoodItemSelected;
import com.pluviostudios.selfimage.planActivity.fragments.BaseMealPlanningFragment;
import com.pluviostudios.selfimage.planActivity.fragments.FoodDetailsDialog;
import com.pluviostudios.selfimage.utilities.MissingExtraException;
import com.pluviostudios.selfimage.utilities.Utilities;
import com.pluviostudios.usdanutritionalapi.FoodItem;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/7/2016.
 */
public class MealPlanFragment extends BaseMealPlanningFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String REFERENCE_ID = "MealPlanFragment";
    public static final String EXTRA_DATE = "date";

    private View mRoot;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private long mDate;

    public static MealPlanFragment buildMealPlanFragment(MealPlanningActivity mealPlanningActivity, long date) {
        MealPlanFragment mealPlanFragment = new MealPlanFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_DATE, date);
        mealPlanFragment.setArguments(args);
        mealPlanFragment.setMealPlanningActivity(mealPlanningActivity);
        return mealPlanFragment;
    }

    private void init() {
        mFloatingActionButton = (FloatingActionButton) mRoot.findViewById(R.id.meal_plan_fragment_FAB);
        mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.meal_plan_fragment_recycler_view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle extras = getArguments();
        if (!extras.containsKey(EXTRA_DATE)) {
            throw new MissingExtraException(EXTRA_DATE);
        }

        mRoot = inflater.inflate(R.layout.meal_plan_fragment, container, false);
        init();

        mDate = extras.getLong(EXTRA_DATE);
        boolean isToday = Utilities.isTodaysDate(mDate);

        if (isToday) {
            mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFoodSearchFragment();
                }
            });
        } else {
            mFloatingActionButton.setVisibility(View.GONE);
        }

        getLoaderManager().initLoader(0, getArguments(), this);

        return mRoot;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getContext(),
                DatabaseContract.DiaryEntry.buildDiaryWithStartDate(mDate),
                new String[]{
                        DatabaseContract.FoodEntry.ITEM_NAME_COL,
                        DatabaseContract.DiaryEntry.ITEM_NDBNO_COL,
                        DatabaseContract.DiaryEntry.ITEM_QUANTITY_COL,
                        DatabaseContract.CategoryEntry.CATEGORY_INDEX_COL,
                        DatabaseContract.CategoryEntry.CATEGORY_NAME_COL
                },
                null,
                null,
                DatabaseContract.DiaryEntry.ITEM_CATEGORY_COL + " ASC "
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        ArrayList<MealPlanRecyclerAdapter.LabeledRecyclerAdapterItem> mList = new ArrayList<>();

        int currentCat = -1;
        if (data.moveToFirst()) {
            do {
                int newCat = data.getInt(3);
                if (currentCat < newCat) {
                    currentCat = newCat;
                    mList.add(new MealPlanRecyclerAdapter.LabeledRecyclerAdapterItem(data.getString(4)));
                }

                FoodItem foodItem = new FoodItem(data.getString(0), data.getString(1));
                mList.add(new MealPlanRecyclerAdapter.LabeledRecyclerAdapterItem(foodItem));
            } while (data.moveToNext());
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MealPlanRecyclerAdapter adapter = new MealPlanRecyclerAdapter(mList);

        adapter.setOnFoodItemSelected(new OnFoodItemSelected() {
            @Override
            public void onFoodItemSelected(FoodItem foodItem) {

                FoodDetailsDialog detailsDialog = FoodDetailsDialog.buildFoodDetailsDialog(foodItem, new FoodDetailsDialog.OnDialogQuantityConfirm() {
                    @Override
                    public void onDialogQuantityConfirm(FoodItem foodItem, int category, int quantity) {
//                        FoodItemDBHandler.updateDiaryItem(
//                                getContext(),
//                                Utilities.getCurrentNormalizedDate(),
//                                foodItem,
//                                category,
//                                quantity,
//
//                        );
                    }
                });
                detailsDialog.show(getFragmentManager(), FoodDetailsDialog.REFERENCE_ID);
            }
        });

        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
