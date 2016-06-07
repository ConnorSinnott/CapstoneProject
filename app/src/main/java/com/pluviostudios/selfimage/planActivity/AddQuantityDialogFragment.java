package com.pluviostudios.selfimage.planActivity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;

import java.util.MissingFormatArgumentException;

/**
 * Created by Spectre on 6/7/2016.
 */
public class AddQuantityDialogFragment extends DialogFragment {

    public static final String REFERENCE_ID = "AddQuantityDialogFragment";
    public static final String EXTRA_FOOD_ITEM = "FoodItem";

    public interface OnDialogQuantityConfirm {
        void onDialogQuantityConfirm(FoodItem foodItem, int quantity);
    }

    private View mRoot;
    private TextView textViewName;
    private Button buttonAdd, buttonSubtract, buttonConfirm;
    private EditText editTextQuantity;


    private FoodItem mFoodItem;

    private int mQuantity = 1;

    private OnDialogQuantityConfirm onQuantityDialogConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (!args.containsKey(EXTRA_FOOD_ITEM)) {
            throw new MissingFormatArgumentException("FoodItem was not passed in arguments");
        }

        mFoodItem = (FoodItem) args.getSerializable(EXTRA_FOOD_ITEM);

        mRoot = inflater.inflate(R.layout.dialog_add_quantity, container, false);

        textViewName = (TextView) mRoot.findViewById(R.id.dialog_add_quantity_name);
        textViewName.setText(mFoodItem.getName());

        buttonAdd = (Button) mRoot.findViewById(R.id.dialog_add_quantity_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantityUI(++mQuantity);
            }
        });

        buttonSubtract = (Button) mRoot.findViewById(R.id.dialog_add_quantity_subtract);
        buttonSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantityUI(--mQuantity);
            }
        });

        buttonConfirm = (Button) mRoot.findViewById(R.id.dialog_add_quantity_confirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onQuantityDialogConfirm != null) {
                    onQuantityDialogConfirm.onDialogQuantityConfirm(mFoodItem, mQuantity);
                    dismiss();
                }
            }
        });

        editTextQuantity = (EditText) mRoot.findViewById(R.id.dialog_add_quantity_edittext);
        updateQuantityUI(mQuantity);

        return mRoot;

    }

    private void updateQuantityUI(int quantity) {
        editTextQuantity.setText(String.valueOf(quantity));
    }

    public void setOnQuantityDialogConfirm(OnDialogQuantityConfirm onQuantityDialogConfirm) {
        this.onQuantityDialogConfirm = onQuantityDialogConfirm;
    }

    public static AddQuantityDialogFragment createAddQuantityDialogFragment(FoodItem foodItem, OnDialogQuantityConfirm onQuantityDialogFragmentConfirm) {

        AddQuantityDialogFragment fragment = new AddQuantityDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(EXTRA_FOOD_ITEM, foodItem);
        fragment.setArguments(args);

        fragment.setOnQuantityDialogConfirm(onQuantityDialogFragmentConfirm);

        return fragment;

    }

}
