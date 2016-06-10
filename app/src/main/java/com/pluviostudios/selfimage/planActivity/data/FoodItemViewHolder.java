package com.pluviostudios.selfimage.planActivity.data;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;

/**
 * Created by Spectre on 6/7/2016.
 */
public class FoodItemViewHolder extends RecyclerView.ViewHolder {

    public CardView card;
    public TextView name;
    public TextView cal;
    public ProgressBar progressBar;
    public ImageView error;

    public FoodItemViewHolder(View itemView) {
        super(itemView);
        card = (CardView) itemView.findViewById(R.id.food_search_item_card);
        name = (TextView) itemView.findViewById(R.id.food_item_search_name);
        cal = (TextView) itemView.findViewById(R.id.food_item_search_cal);
        progressBar = (ProgressBar) itemView.findViewById(R.id.food_item_progress_bar);
        error = (ImageView) itemView.findViewById(R.id.food_item_search_error);
    }


}
