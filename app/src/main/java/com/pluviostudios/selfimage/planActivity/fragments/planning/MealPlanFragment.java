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
import com.pluviostudios.selfimage.data.database.DatabaseContract;
import com.pluviostudios.selfimage.data.dataContainers.DiaryItem;
import com.pluviostudios.selfimage.data.dataContainers.FoodItemWithDB;
import com.pluviostudios.selfimage.planActivity.MealPlanningActivity;
import com.pluviostudios.selfimage.planActivity.fragments.BaseMealPlanningFragment;
import com.pluviostudios.selfimage.planActivity.fragments.FoodDetailsDialog;
import com.pluviostudios.selfimage.utilities.MissingExtraException;
import com.pluviostudios.selfimage.utilities.Utilities;

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

                int itemQuantity = data.getInt(2);
                int itemCategory = data.getInt(3);

                if (currentCat < itemCategory) {
                    currentCat = itemCategory;
                    mList.add(new MealPlanRecyclerAdapter.LabeledRecyclerAdapterItem(data.getString(4)));
                }

                String foodName = data.getString(0);
                String foodNDBNO = data.getString(1);

                FoodItemWithDB foodItemWithDB = new FoodItemWithDB(foodName, foodNDBNO);
                DiaryItem diaryItem = new DiaryItem(foodItemWithDB, mDate, itemCategory, itemQuantity);

                mList.add(new MealPlanRecyclerAdapter.LabeledRecyclerAdapterItem(diaryItem));

            } while (data.moveToNext());

        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MealPlanRecyclerAdapter adapter = new MealPlanRecyclerAdapter(mList);

        adapter.setOnDiaryItemSelected(new MealPlanRecyclerAdapter.OnDiaryItemSelected() {

            @Override
            public void onDiaryItemSelected(DiaryItem diaryItem) {
                showFoodDetailsDialog(diaryItem);
            }

        });

        mRecyclerView.setAdapter(adapter);

    }

    private void showFoodDetailsDialog(DiaryItem diaryItem) {

        FoodDetailsDialog detailsDialog = FoodDetailsDialog.buildFoodDetailsDialog(diaryItem, new FoodDetailsDialog.OnDetailsDialogConfirm() {
            @Override
            public void onDetailsDialogConfirm(DiaryItem diaryItem) {
                diaryItem.save();
            }
        });

        detailsDialog.show(getFragmentManager(), FoodDetailsDialog.REFERENCE_ID);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
