package com.pluviostudios.selfimage.planActivity;

/**
 * Created by Spectre on 6/2/2016.
 */
public class FoodItemNetworkContainer {

    public static final int ERROR = -1;
    public static final int AWAITING_UPDATE = 100;
    public static final int UPDATING = 200;
    public static final int COMPLETE = 300;

    public FoodItem foodItem;
    public int status = AWAITING_UPDATE;

    public FoodItemNetworkContainer(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

}
