package com.pluviostudios.selfimage.planActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;

import java.util.ArrayList;

/**
 * Created by Spectre on 5/24/2016.
 */
public class FoodSearchFragment extends Fragment {

    public static final String REFERENCE_TAG = "FoodSearchFragment";

    private CountDownTimer mCountDownTimer;

    private View mRoot;
    private EditText mEditText;
    private RecyclerView mListView;
    private TextView mTextResultCount;

    private FoodSearchRecyclerAdapter mAdapter;

    private AsyncNDBNOSearch mAsyncNDBNOSearch;
    private AsyncReportSearch mAsyncReportSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRoot = inflater.inflate(R.layout.add_food_fragment, null, false);

        mListView = (RecyclerView) mRoot.findViewById(R.id.add_food_fragment_recycler_view);
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

        mListView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new FoodSearchRecyclerAdapter();
        mAdapter.setOnFoodItemClickListener(new FoodSearchRecyclerAdapter.OnFoodItemClickListener() {
            @Override
            public void onFoodItemClicked(FoodItem foodItem) {
                showQuantityDialogFragment(foodItem);
            }
        });

        mListView.setAdapter(mAdapter);

        return mRoot;
    }

    private void showQuantityDialogFragment(FoodItem foodItem) {
        AddQuantityDialogFragment fragment = AddQuantityDialogFragment.createAddQuantityDialogFragment(foodItem, new AddQuantityDialogFragment.OnDialogQuantityConfirm() {
            @Override
            public void onDialogQuantityConfirm(FoodItem foodItem, int quantity) {
                addFoodToPlan(foodItem, quantity);
            }
        });
        fragment.show(getFragmentManager(), AddQuantityDialogFragment.REFERENCE_ID);
    }

    private void addFoodToPlan(FoodItem foodItem, int quantity) {
        //TODO Make undo snackbar
        Snackbar.make(mRoot, foodItem.getName() + " " + quantity, Snackbar.LENGTH_LONG).show();
    }

    private void searchForNDBNO() {
        clearAll();
        mAsyncNDBNOSearch = new AsyncNDBNOSearch(getContext(), new AsyncNDBNOSearch.OnAsyncNDBNOSearchResult() {
            @Override
            public void onResult(ArrayList<FoodItemNetworkContainer> data) {
                mTextResultCount.setText(String.valueOf(data.size()) + " results");
                for (FoodItemNetworkContainer x : data) {
                    mAdapter.addFoodItem(x);
                }
                searchForReports(data);
            }
        });
        mAsyncNDBNOSearch.execute(mEditText.getText().toString());
    }

    private void searchForReports(ArrayList<FoodItemNetworkContainer> foodItemList) {
        mAsyncReportSearch = new AsyncReportSearch(getContext(), new AsyncReportSearch.OnAsyncReportSearchResult() {
            @Override
            public void onResult(FoodItemNetworkContainer data) {
                mAdapter.updateFoodItems();
            }
        });
        mAsyncReportSearch.execute(foodItemList);
    }

    private void clearAll() {
        if (mAsyncNDBNOSearch != null && !mAsyncNDBNOSearch.isCancelled())
            mAsyncNDBNOSearch.cancel(true);
        if (mAsyncReportSearch != null && !mAsyncReportSearch.isCancelled())
            mAsyncReportSearch.cancel(true);
        mAdapter.clearAll();
    }

}
