package com.pluviostudios.selfimage.planActivity.fragments.planning;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.planActivity.data.OnFoodItemSelected;
import com.pluviostudios.usdanutritionalapi.FoodItem;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/19/2016.
 */
public class MealPlanRecyclerAdapter extends RecyclerView.Adapter<MealPlanRecyclerAdapter.NamedViewHolder> {

    private static final int VIEW_TYPE_LABEL = 0;
    private static final int VIEW_TYPE_FOOD = 1;

    Context mContext;

    private OnFoodItemSelected mOnFoodItemSelected;
    private ArrayList<LabeledRecyclerAdapterItem> mData = new ArrayList<>();

    public void setOnFoodItemSelected(OnFoodItemSelected onFoodItemSelected) {
        mOnFoodItemSelected = onFoodItemSelected;
    }

    public MealPlanRecyclerAdapter(ArrayList<LabeledRecyclerAdapterItem> mList) {
        mData = mList;
    }

    @Override
    public NamedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int viewResource = (viewType == VIEW_TYPE_LABEL) ? R.layout.food_search_divider : R.layout.food_search_item;
        View v = LayoutInflater.from(parent.getContext()).inflate(viewResource, parent, false);
        NamedViewHolder viewHolder;
        if (viewType == VIEW_TYPE_LABEL) {
            viewHolder = new MealPlanRecyclerAdapter.NamedViewHolder.LabelViewHolder(v);
        } else {
            viewHolder = new MealPlanRecyclerAdapter.NamedViewHolder.FoodItemViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NamedViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_LABEL) {
            holder.name.setText(mData.get(position).getLabel());
        } else {
            FoodItem foodItem = mData.get(position).getFoodItem();
            holder.name.setText(foodItem.getFoodName());
            holder.root.setOnClickListener(new View.OnClickListener() {

                private FoodItem mItem;

                @Override
                public void onClick(View v) {
                    if (mOnFoodItemSelected != null) {
                        mOnFoodItemSelected.onFoodItemSelected(mItem);
                    }
                }

                public View.OnClickListener setItem(FoodItem item) {
                    mItem = item;
                    return this;
                }

            }.setItem(foodItem));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        LabeledRecyclerAdapterItem item = mData.get(position);
        return item.isLabel() ? VIEW_TYPE_LABEL : VIEW_TYPE_FOOD;
    }

    /**
     * Created by Spectre on 6/9/2016.
     */
    public static class LabeledRecyclerAdapterItem {

        private FoodItem mFoodItem;
        private String mLabel;

        public boolean isLabel() {
            return mLabel != null;
        }

        public LabeledRecyclerAdapterItem(FoodItem foodItem) {
            mFoodItem = foodItem;
        }

        public LabeledRecyclerAdapterItem(String label) {
            mLabel = label;
        }

        public FoodItem getFoodItem() {
            return mFoodItem;
        }

        public String getLabel() {
            return mLabel;
        }

    }

    /**
     * Created by Spectre on 6/9/2016.
     */
    protected static class NamedViewHolder extends RecyclerView.ViewHolder {

        public static final int VIEW_TYPE_LABEL = 0;
        public static final int VIEW_TYPE_FOOD = 1;

        public View root;
        public TextView name;

        public NamedViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * Created by Spectre on 6/9/2016.
         */
        protected static class LabelViewHolder extends NamedViewHolder {

            public LabelViewHolder(View itemView) {
                super(itemView);
                root = itemView;
                name = (TextView) itemView.findViewById(R.id.food_search_divider_text);
            }

        }

        /**
         * Created by Spectre on 6/7/2016.
         */
        protected static class FoodItemViewHolder extends NamedViewHolder {

            public CardView card;
            public TextView rightText;
            public ImageButton mImageButton;

            public FoodItemViewHolder(View itemView) {
                super(itemView);
                root = itemView;
                name = (TextView) itemView.findViewById(R.id.food_item_search_name);
                card = (CardView) itemView.findViewById(R.id.food_search_item_card);
                //            rightText = (TextView) itemView.findViewById(R.id.food_item_right_text);
                mImageButton = (ImageButton) itemView.findViewById(R.id.food_search_item_button);
            }

        }
    }

}
