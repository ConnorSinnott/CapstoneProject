package com.pluviostudios.selfimage.planActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Spectre on 5/26/2016.
 */
public class FoodItem implements Serializable {

    private String mName;
    private String mNdbmo;

    public static final int Calories = 0;
    public static final int Protein = 1;
    public static final int Fat = 2;
    public static final int Carbs = 3;
    public static final int Fiber = 4;
    public static final int SatFat = 5;
    public static final int MonoFat = 6;
    public static final int PolyFat = 7;
    public static final int Cholesterol = 8;

    public static final ArrayList<Integer> nutrientIds = new ArrayList<>();

    static {
        nutrientIds.add(Calories, 208);
        nutrientIds.add(Protein, 203);
        nutrientIds.add(Fat, 204);
        nutrientIds.add(Carbs, 205);
        nutrientIds.add(Fiber, 291);
        nutrientIds.add(SatFat, 606);
        nutrientIds.add(MonoFat, 645);
        nutrientIds.add(PolyFat, 646);
        nutrientIds.add(Cholesterol, 601);
    }

    private HashMap<Integer, Double> nutrientData = new HashMap<>();

    public FoodItem(String name, String ndbmo) {
        mName = name;
        mNdbmo = ndbmo;
    }

    public String getName() {
        return mName;
    }

    public String getNdbmo() {
        return mNdbmo;
    }

    public boolean hasData() {
        return nutrientData.size() > 0;
    }

    public double getValue(int id) {
        Double value;
        if ((value = nutrientData.get(id)) != null) {
            return value;
        } else {
            return -1;
        }
    }

    public void setValue(int id, double value) {
        nutrientData.put(id, value);
    }

}
