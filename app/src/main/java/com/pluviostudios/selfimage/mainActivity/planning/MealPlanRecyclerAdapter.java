package com.pluviostudios.selfimage.mainActivity.planning;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.dataContainers.diary.DiaryItem;
import com.pluviostudios.selfimage.data.dataContainers.food.FoodItemWithDB;
import com.pluviostudios.selfimage.detailsFragment.FoodDetailsDialog;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/19/2016.
 */
public class MealPlanRecyclerAdapter extends RecyclerView.Adapter<MealPlanRecyclerAdapter.NamedViewHolder> {

    private static final int VIEW_TYPE_LABEL = 0;
    private static final int VIEW_TYPE_FOOD = 1;

    private Fragment mParent;
    private ArrayList<LabeledRecyclerAdapterItem> mData = new ArrayList<>();

    public MealPlanRecyclerAdapter(Fragment parent, ArrayList<LabeledRecyclerAdapterItem> mList) {
        mParent = parent;
        mData = mList;
    }

    @Override
    public NamedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Determine whether this is a Label or a DiaryItem
        int viewResource = (viewType == VIEW_TYPE_LABEL) ? R.layout.diary_item_label : R.layout.diary_item;

        // Inflate the view
        View v = LayoutInflater.from(parent.getContext()).inflate(viewResource, parent, false);

        // Create view holder
        NamedViewHolder viewHolder;
        if (viewType == VIEW_TYPE_LABEL) {
            viewHolder = new MealPlanRecyclerAdapter.NamedViewHolder.LabelViewHolder(v);
        } else {
            viewHolder = new NamedViewHolder.DiaryItemViewHolder(v);
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(NamedViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_LABEL) {

            // Display the label name
            holder.textName.setText(mData.get(position).getLabel());

        } else {

            // Get diary and food item for this position
            DiaryItem diaryItem = mData.get(position).getDiaryItem();
            FoodItemWithDB foodItem = diaryItem.foodItem;

            NamedViewHolder.DiaryItemViewHolder diaryItemVH = (NamedViewHolder.DiaryItemViewHolder) holder;

            // Set Name
            diaryItemVH.textName.setText(foodItem.foodName + " x" + diaryItem.quantity);

            // Make a request for nutritional data for this food item
            foodItem.getNutrientDataWithDB(mParent.getContext(), new FoodItemWithDB.OnDataPulledWithDB() {

                NamedViewHolder.DiaryItemViewHolder vh;
                DiaryItem diaryItem;

                public FoodItemWithDB.OnDataPulledWithDB setData(NamedViewHolder.DiaryItemViewHolder vh, DiaryItem diaryItem) {
                    this.vh = vh;
                    this.diaryItem = diaryItem;
                    return this;
                }

                @Override
                public void onDataPulled(FoodItemWithDB foodItemWithDB) {
                    // Display the food item's calories
                    int calories = (int) Math.round(foodItemWithDB.getNutrientData().get(FoodItemWithDB.Calories)) * diaryItem.quantity;
                    vh.textCalories.setText(mParent.getString(R.string.calorie_view, calories));
                }

            }.setData(diaryItemVH, diaryItem));

            // ButtonEdit -> Show food detail dialog
            diaryItemVH.buttonEdit.setOnClickListener(new OnClickListenerWithDiaryItem() {
                @Override
                public void onClick(View v) {

                    FoodDetailsDialog detailsDialog = FoodDetailsDialog.buildFoodDetailsDialog(mItem, new FoodDetailsDialog.OnDetailsDialogConfirm() {
                        @Override
                        public void onDetailsDialogConfirm(DiaryItem diaryItem) {
                            diaryItem.save(mParent.getContext());
                        }
                    });
                    detailsDialog.show(mParent.getFragmentManager(), FoodDetailsDialog.REFERENCE_ID);

                }
            }.setItem(diaryItem));

            // ButtonDelete -> Delete diary item from database
            diaryItemVH.buttonDelete.setOnClickListener(new OnClickListenerWithDiaryItem() {

                @Override
                public void onClick(View v) {

                    mItem.delete(mParent.getContext());

                }

            }.setItem(diaryItem));

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

    public abstract class OnClickListenerWithDiaryItem implements View.OnClickListener {

        DiaryItem mItem;

        public View.OnClickListener setItem(DiaryItem item) {
            mItem = item;
            return this;
        }

    }

    /**
     * Created by Spectre on 6/9/2016.
     */
    public static class LabeledRecyclerAdapterItem {

        private DiaryItem mDiaryItem;
        private String mLabel;

        public boolean isLabel() {
            return mLabel != null;
        }

        public LabeledRecyclerAdapterItem(DiaryItem diaryItem) {
            mDiaryItem = diaryItem;
        }

        public LabeledRecyclerAdapterItem(String label) {
            mLabel = label;
        }

        public DiaryItem getDiaryItem() {
            return mDiaryItem;
        }

        public String getLabel() {
            return mLabel;
        }

    }

    /**
     * Created by Spectre on 6/9/2016.
     */
    protected static class NamedViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public TextView textName;

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
                textName = (TextView) itemView.findViewById(R.id.food_search_divider_text);
            }

        }

        /**
         * Created by Spectre on 6/7/2016.
         */
        protected static class DiaryItemViewHolder extends NamedViewHolder {

            public TextView textCalories;
            public ImageButton buttonEdit;
            public ImageButton buttonDelete;

            public DiaryItemViewHolder(View itemView) {
                super(itemView);
                root = itemView;
                textName = (TextView) itemView.findViewById(R.id.diary_item_name);
                textCalories = (TextView) itemView.findViewById(R.id.diary_item_calorie);
                buttonEdit = (ImageButton) itemView.findViewById(R.id.diary_item_edit);
                buttonDelete = (ImageButton) itemView.findViewById(R.id.diary_item_delete);
            }

        }
    }

}
