package com.pluviostudios.selfimage.planActivity.planning;

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
import com.pluviostudios.selfimage.planActivity.activity.BaseMealPlanningFragment;
import com.pluviostudios.selfimage.planActivity.data.FoodItem;
import com.pluviostudios.selfimage.utilities.Utilities;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/7/2016.
 */
public class MealPlanFragment extends BaseMealPlanningFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String REFERENCE_ID = "MealPlanFragment";

    private View mRoot;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.meal_plan_fragment, container, false);

        mFloatingActionButton = (FloatingActionButton) mRoot.findViewById(R.id.meal_plan_fragment_FAB);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodSearchFragment();
            }
        });

        mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.meal_plan_fragment_recycler_view);

        getLoaderManager().initLoader(0, null, this);

        return mRoot;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                DatabaseContract.DiaryEntry.buildDiaryWithStartDate(Utilities.getCurrentNormalizedDate()),
                new String[]{
                        DatabaseContract.DiaryEntry.ITEM_NAME_COL,
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

        ArrayList<MealPlanFragmentRecyclerItem> mList = new ArrayList<>();

        int currentCat = -1;
        if (data.moveToFirst()) {
            int newCat = data.getInt(3);
            if (currentCat < newCat) {
                currentCat = newCat;
                mList.add(new MealPlanFragmentRecyclerItem(data.getString(4)));
            }

            FoodItem tempFoodItem = new FoodItem(
                    data.getString(0),
                    data.getString(2)
            );

            mList.add(new MealPlanFragmentRecyclerItem(tempFoodItem));
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new MealPlanFragmentRecyclerAdapter(mList));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
