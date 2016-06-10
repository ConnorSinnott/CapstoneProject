package com.pluviostudios.selfimage.planActivity.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.planActivity.data.FoodItem;
import com.pluviostudios.selfimage.planActivity.data.FoodItemNetworkContainer;
import com.pluviostudios.selfimage.planActivity.data.FoodItemViewHolder;

import java.util.ArrayList;

/**
 * Created by Spectre on 5/26/2016.
 */
public class FoodSearchRecyclerAdapter extends RecyclerView.Adapter<FoodItemViewHolder> {

    private OnFoodItemClickListener mOnFoodItemClickListener;

    private ArrayList<FoodItemNetworkContainer> mFoodData = new ArrayList<>();

    @Override
    public FoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_search_item, parent, false);
        FoodItemViewHolder viewHolder = new FoodItemViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {
        FoodItemNetworkContainer itemContainer = mFoodData.get(position);
        FoodItem item = itemContainer.foodItem;

        if (mOnFoodItemClickListener != null) {
            holder.card.setOnClickListener(new View.OnClickListener() {
                int currPosition;

                @Override
                public void onClick(View v) {
                    mOnFoodItemClickListener.onFoodItemClicked(mFoodData.get(currPosition).foodItem);
                }

                public View.OnClickListener setPosition(int position) {
                    currPosition = position;
                    return this;
                }

            }.setPosition(position));
        }

        holder.name.setText(item.getName());
        holder.progressBar.setVisibility(View.INVISIBLE);
        holder.error.setVisibility(View.INVISIBLE);
        holder.cal.setText("");

        switch (itemContainer.status)

        {
            case FoodItemNetworkContainer.AWAITING_UPDATE:
                break;
            case FoodItemNetworkContainer.UPDATING:
                holder.progressBar.setVisibility(View.VISIBLE);
                break;
            case FoodItemNetworkContainer.COMPLETE:
                holder.cal.setText(String.valueOf((int) item.getValue(FoodItem.Calories)));
                break;
            case FoodItemNetworkContainer.ERROR:
                holder.error.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return mFoodData.size();
    }

    public void addFoodItem(FoodItemNetworkContainer foodItem) {
        mFoodData.add(foodItem);
        notifyDataSetChanged();
    }

    public void updateFoodItems() {
        notifyDataSetChanged();
    }

    public void clearAll() {
        if (mFoodData.size() > 0) {
            mFoodData = new ArrayList<>();
            notifyDataSetChanged();
        }
    }

    public interface OnFoodItemClickListener {
        void onFoodItemClicked(FoodItem foodItem);
    }

    public void setOnFoodItemClickListener(OnFoodItemClickListener onFoodItemClickListener) {
        mOnFoodItemClickListener = onFoodItemClickListener;
    }

}
