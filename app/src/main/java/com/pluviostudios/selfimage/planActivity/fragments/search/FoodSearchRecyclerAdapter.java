package com.pluviostudios.selfimage.planActivity.fragments.search;

/**
 * Created by Spectre on 6/20/2016.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.planActivity.data.OnFoodItemSelected;
import com.pluviostudios.usdanutritionalapi.FoodItem;

import java.util.ArrayList;

/**
 * Created by Spectre on 5/26/2016.
 */
public class FoodSearchRecyclerAdapter extends RecyclerView.Adapter<FoodSearchRecyclerAdapter.FoodItemViewHolder> {

    private OnFoodItemSelected mOnFoodItemSelected;

    private ArrayList<FoodItem> mFoodData = new ArrayList<>();

    public void setOnFoodItemSelected(OnFoodItemSelected onFoodItemSelected) {
        mOnFoodItemSelected = onFoodItemSelected;
    }

    @Override
    public FoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_search_item, parent, false);
        FoodItemViewHolder viewHolder = new FoodItemViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {
        FoodItem item = mFoodData.get(position);

        if (mOnFoodItemSelected != null) {
            holder.card.setOnClickListener(new View.OnClickListener() {
                int currPosition;

                @Override
                public void onClick(View v) {
                    mOnFoodItemSelected.onFoodItemSelected(mFoodData.get(currPosition));
                }

                public View.OnClickListener setPosition(int position) {
                    currPosition = position;
                    return this;
                }

            }.setPosition(position));
        }

        holder.name.setText(item.getFoodName());
    }


    @Override
    public int getItemCount() {
        return mFoodData.size();
    }

    public void addFoodItem(FoodItem foodItem) {
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

    /**
     * Created by Spectre on 6/7/2016.
     */
    protected static class FoodItemViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public TextView name;
        public CardView card;

        public FoodItemViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            name = (TextView) itemView.findViewById(R.id.food_item_search_name);
            card = (CardView) itemView.findViewById(R.id.food_search_item_card);
        }

    }

}
