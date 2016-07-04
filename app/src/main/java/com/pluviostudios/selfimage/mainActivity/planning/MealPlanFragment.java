package com.pluviostudios.selfimage.mainActivity.planning;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.dataContainers.diary.DiaryItem;
import com.pluviostudios.selfimage.data.dataContainers.diary.DiaryItemLoaderCallbacks;
import com.pluviostudios.selfimage.data.dataContainers.diary.DiaryItemNutrientTotals;
import com.pluviostudios.selfimage.data.dataContainers.food.FoodItemWithDB;
import com.pluviostudios.selfimage.searchActivity.FoodSearchActivity;
import com.pluviostudios.selfimage.utilities.MissingExtraException;
import com.pluviostudios.selfimage.views.CalorieBar;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/7/2016.
 */
public class MealPlanFragment extends Fragment {

    public static final String REFERENCE_ID = "MealPlanFragment";
    public static final String EXTRA_DATE = "date";

    private View mRoot;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private CalorieBar mCalBar;
    private long mDate;

    public static MealPlanFragment buildMealPlanFragment(long date) {
        MealPlanFragment mealPlanFragment = new MealPlanFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_DATE, date);
        mealPlanFragment.setArguments(args);
        return mealPlanFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // If date is not passed as an extra, throw exception
        Bundle extras = getArguments();
        if (!extras.containsKey(EXTRA_DATE)) {
            throw new MissingExtraException(EXTRA_DATE);
        }

        // Initialize views
        mRoot = inflater.inflate(R.layout.fragment_meal_planning, container, false);
        mFloatingActionButton = (FloatingActionButton) mRoot.findViewById(R.id.fragment_meal_planning_add_FAB);
        mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.fragment_meal_planning_RecyclerView);
        mCalBar = (CalorieBar) mRoot.findViewById(R.id.fragment_meal_planning_calorie_bar);

        // Get date from extras
        mDate = extras.getLong(EXTRA_DATE);

        // Get the caloric nutritional total from all items within date's diary
        DiaryItem.getNutrientTotals(getContext(), getLoaderManager(), 1, mDate, null, null, new DiaryItemNutrientTotals.OnNutrientTotalsReceived() {
            @Override
            public void onNutrientTotalsReceived(ArrayList<Double> totals) {
                // Display it on the CalorieBar
                mCalBar.setProgress((int) Math.round(totals.get(FoodItemWithDB.Calories)));
            }
        });

        // FAB -> Start FoodSearchActivity with date
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FoodSearchActivity.class);
                intent.putExtra(FoodSearchActivity.EXTRA_DATE, mDate);
                startActivity(intent);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        populateAdapter();

        return mRoot;
    }

    private void populateAdapter() {

        // Get all of date's diary items and display them on the Recycler Adapter
        DiaryItem.getDiaryItems(getContext(), getLoaderManager(), 0, mDate, null, null, new DiaryItemLoaderCallbacks.OnDiaryItemsReceived() {
            @Override
            public void onDiaryItemsReceived(ArrayList<DiaryItem> diaryItems) {

                ArrayList<MealPlanRecyclerAdapter.LabeledRecyclerAdapterItem> mList = new ArrayList<>();

                int currentCat = -1;
                for (DiaryItem x : diaryItems) {

                    // Create a category label for each category
                    if (currentCat < x.category) {
                        currentCat = x.category;

                        mList.add(new MealPlanRecyclerAdapter.LabeledRecyclerAdapterItem(x.getCategoryName(getContext())));
                    }

                    // Create a view for the DiaryItem
                    mList.add(new MealPlanRecyclerAdapter.LabeledRecyclerAdapterItem(x));

                }

                MealPlanRecyclerAdapter adapter = new MealPlanRecyclerAdapter(MealPlanFragment.this, mList);
                mRecyclerView.setAdapter(adapter);

            }
        });

    }

}
