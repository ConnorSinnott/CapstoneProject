package com.pluviostudios.selfimage.planActivity.fragments.search;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.planActivity.MealPlanningActivity;
import com.pluviostudios.selfimage.planActivity.data.OnFoodItemSelected;
import com.pluviostudios.selfimage.planActivity.fragments.BaseMealPlanningFragment;
import com.pluviostudios.selfimage.planActivity.data.FoodItemDBHandler;
import com.pluviostudios.selfimage.planActivity.fragments.FoodDetailsDialog;
import com.pluviostudios.selfimage.utilities.Utilities;
import com.pluviostudios.usdanutritionalapi.AsyncFoodItemSearch;
import com.pluviostudios.usdanutritionalapi.FoodItem;

import java.util.ArrayList;

/**
 * Created by Spectre on 5/24/2016.
 */
public class FoodSearchFragment extends BaseMealPlanningFragment {

    public static final String REFERENCE_TAG = "FoodSearchFragment";

    private CountDownTimer mCountDownTimer;

    private View mRoot;
    private EditText mEditText;
    private Button mButtonScan;
    private RecyclerView mListView;
    private TextView mTextResultCount;

    private FoodSearchRecyclerAdapter mAdapter;

    private AsyncFoodItemSearch mAsyncNDBNOSearch;

    public static FoodSearchFragment buildFoodSearchFragment(MealPlanningActivity mealPlanningActivity) {
        FoodSearchFragment foodSearchFragment = new FoodSearchFragment();
        foodSearchFragment.setMealPlanningActivity(mealPlanningActivity);
        return foodSearchFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRoot = inflater.inflate(R.layout.add_food_fragment, null, false);

        mTextResultCount = (TextView) mRoot.findViewById(R.id.add_food_fragment_result_count);

        mEditText = (EditText) mRoot.findViewById(R.id.add_food_fragment_edit_text);
        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


            @Override
            public void afterTextChanged(Editable s) {
                clearAll();

                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                mCountDownTimer = new CountDownTimer(1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        searchForNDBNO();
                    }

                }.start();

            }
        });

        mListView = (RecyclerView) mRoot.findViewById(R.id.add_food_fragment_recycler_view);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new FoodSearchRecyclerAdapter();
        mAdapter.setOnFoodItemSelected(new OnFoodItemSelected() {
            @Override
            public void onFoodItemSelected(FoodItem foodItem) {

                FoodDetailsDialog fragment = FoodDetailsDialog.buildFoodDetailsDialog(foodItem, new FoodDetailsDialog.OnDialogQuantityConfirm() {
                    @Override
                    public void onDialogQuantityConfirm(FoodItem foodData, int category, int quantity) {
                        FoodItemDBHandler.insertIntoMealPlan(getContext(), Utilities.getCurrentNormalizedDate(), foodData, quantity, category);
                        Snackbar.make(mRoot, "Added " + foodData.getFoodName(), Snackbar.LENGTH_SHORT).show();
                    }
                });
                fragment.show(getFragmentManager(), FoodDetailsDialog.REFERENCE_ID);

            }
        });

        mListView.setAdapter(mAdapter);

        mButtonScan = (Button) mRoot.findViewById(R.id.add_food_fragment_button_scan);
        mButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Add Scanning
            }
        });

        return mRoot;
    }

    private void searchForNDBNO() {
        clearAll();
        mAsyncNDBNOSearch = new AsyncFoodItemSearch(getContext(), new AsyncFoodItemSearch.OnAsyncNDBNOSearchResult() {
            @Override
            public void onResult(ArrayList<FoodItem> data) {
                mTextResultCount.setText(getString(R.string.results_found, data.size()));
                for (FoodItem x : data) {
                    mAdapter.addFoodItem(x);
                }
            }
        });
        mAsyncNDBNOSearch.execute(mEditText.getText().toString());
    }

    private void clearAll() {
        if (mAsyncNDBNOSearch != null && !mAsyncNDBNOSearch.isCancelled())
            mAsyncNDBNOSearch.cancel(true);
        mAdapter.clearAll();
    }

}
