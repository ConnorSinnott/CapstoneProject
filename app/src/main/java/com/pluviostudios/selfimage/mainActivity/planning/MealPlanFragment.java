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
import com.pluviostudios.selfimage.searchActivity.FoodSearchActivity;
import com.pluviostudios.selfimage.utilities.DateUtils;
import com.pluviostudios.selfimage.utilities.MissingExtraException;

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

        Bundle extras = getArguments();
        if (!extras.containsKey(EXTRA_DATE)) {
            throw new MissingExtraException(EXTRA_DATE);
        }

        mRoot = inflater.inflate(R.layout.meal_plan_fragment, container, false);
        mFloatingActionButton = (FloatingActionButton) mRoot.findViewById(R.id.meal_plan_fragment_FAB);
        mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.meal_plan_fragment_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mDate = extras.getLong(EXTRA_DATE);
        boolean isToday = DateUtils.isTodaysDate(mDate);

        if (isToday) {
            mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FoodSearchActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            mFloatingActionButton.setVisibility(View.GONE);
        }

        populateAdapter();

        return mRoot;
    }

    private void populateAdapter() {

        DiaryItem.getDiaryItems(getContext(), getLoaderManager(), 0, mDate, new DiaryItemLoaderCallbacks.OnDiaryItemsReceived() {
            @Override
            public void onDiaryItemsReceived(ArrayList<DiaryItem> diaryItems) {

                ArrayList<MealPlanRecyclerAdapter.LabeledRecyclerAdapterItem> mList = new ArrayList<>();

                int currentCat = -1;
                for (DiaryItem x : diaryItems) {

                    if (currentCat < x.category) {
                        currentCat = x.category;
                        mList.add(new MealPlanRecyclerAdapter.LabeledRecyclerAdapterItem(x.getCategoryName(getContext())));
                    }

                    mList.add(new MealPlanRecyclerAdapter.LabeledRecyclerAdapterItem(x));

                }

                MealPlanRecyclerAdapter adapter = new MealPlanRecyclerAdapter(MealPlanFragment.this, mList);
                mRecyclerView.setAdapter(adapter);

            }
        });
    }

}
