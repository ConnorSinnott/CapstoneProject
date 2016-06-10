package com.pluviostudios.selfimage.planActivity.planning;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.planActivity.data.FoodItemViewHolder;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/7/2016.
 */
public class MealPlanFragmentRecyclerAdapter extends RecyclerView.Adapter<FoodItemViewHolder> {

    private ArrayList<MealPlanFragmentRecyclerItem> mData;

    public MealPlanFragmentRecyclerAdapter(ArrayList<MealPlanFragmentRecyclerItem> itemList) {
        mData = itemList;
    }

    @Override
    public FoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_search_item, parent, false);
        FoodItemViewHolder viewHolder = new FoodItemViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


}
