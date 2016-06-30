package com.pluviostudios.selfimage.searchActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.dataContainers.diary.DiaryItem;
import com.pluviostudios.selfimage.data.dataContainers.food.FoodItemWithDB;
import com.pluviostudios.selfimage.mainActivity.planning.FoodDetailsDialog;
import com.pluviostudios.selfimage.utilities.MissingExtraException;
import com.pluviostudios.selfimage.utilities.NetworkUtils;
import com.pluviostudios.usdanutritionalapi.AsyncFoodItemSearch;
import com.pluviostudios.usdanutritionalapi.FoodItem;

import java.util.ArrayList;

/**
 * Created by Spectre on 5/24/2016.
 */
public class FoodSearchActivity extends AppCompatActivity {

    public static final String REFERENCE_ID = "FoodSearchActivity";

    public static final String EXTRA_DATE = "extra_date";

    private EditText mEditText;
    private Button mButtonScan;
    private RecyclerView mListView;

    private long mDate;
    private CountDownTimer mCountDownTimer;
    private FoodSearchRecyclerAdapter mAdapter;

    // How will search results be handled
    private final AsyncFoodItemSearch.OnAsyncNDBNOSearchResult mOnAsyncNDBNOSearchResult = new AsyncFoodItemSearch.OnAsyncNDBNOSearchResult() {
        @Override
        public void onResult(ArrayList<FoodItem> data) {

            // Display Results
            for (FoodItem x : data) {
                mAdapter.addFoodItem(new FoodItemWithDB(x));
            }

        }
    };

    //What happens when the user selects an item
    private final FoodDetailsDialog.OnDetailsDialogConfirm mOnDetailsDialogConfirm = new FoodDetailsDialog.OnDetailsDialogConfirm() {
        @Override
        public void onDetailsDialogConfirm(DiaryItem diaryItem) {

            // Save item to database
            diaryItem.save(getApplicationContext());

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_food_search);

        if (!getIntent().hasExtra(EXTRA_DATE)) {
            throw new MissingExtraException(EXTRA_DATE);
        }

        mDate = getIntent().getExtras().getLong(EXTRA_DATE);

        // Init views
        mEditText = (EditText) findViewById(R.id.add_food_fragment_edit_text);
        mListView = (RecyclerView) findViewById(R.id.add_food_fragment_recycler_view);
        mButtonScan = (Button) findViewById(R.id.add_food_fragment_button_scan);

        if (!NetworkUtils.hasNetworkConnection(getApplicationContext())) {
            Snackbar.make(mListView, R.string.no_network_connection, Snackbar.LENGTH_LONG).show();
            mEditText.setEnabled(false);
            mEditText.setFocusable(false);
            mButtonScan.setEnabled(false);
        }

        // Do search if text is left unchanged for 1 second
        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mAdapter.clearAll();
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                mCountDownTimer = new CountDownTimer(1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        searchForFoodItemsWithTags(mEditText.getText().toString());
                    }

                }.start();
            }

        });

        // Setup Adapter
        mAdapter = new FoodSearchRecyclerAdapter();
        mAdapter.setOnFoodItemSelected(new FoodSearchRecyclerAdapter.OnFoodItemSelected() {
            @Override
            public void onFoodItemSelected(FoodItemWithDB foodItem) {
                FoodDetailsDialog dialog = FoodDetailsDialog.buildFoodDetailsDialog(foodItem, mDate, mOnDetailsDialogConfirm);
                dialog.show(getSupportFragmentManager(), FoodDetailsDialog.REFERENCE_ID);
            }
        });

        // Setup list view
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);

        // Launch scan activity on button click
        mButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchScanActivity();
            }
        });


    }

    private void launchScanActivity() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setBarcodeImageEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }


    private void searchForFoodItemsWithTags(String searchString) {
        mAdapter.clearAll();
        FoodItemWithDB.getFoodItems(this, mOnAsyncNDBNOSearchResult, searchString);
    }

    private void searchForFoodItemsWithUPC(String upc) {
        new AsyncProcessBarcode(getString(R.string.outpan_api), new AsyncProcessBarcode.OnBarcodeProcessed() {
            @Override
            public void onBarcodeProcessed(final String UPC, final String itemName) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Barcode Result: " + UPC)
                        .setIcon(android.R.drawable.ic_dialog_info);


                String message = "At the moment, SelfImage does not have access to a database " +
                        "which can seamlessly provide nutritional information from a UPC. \n \n";

                if (itemName != null) {

                    message +=
                            "We can attempt a search based on the product listed below, however " +
                                    "you will likely need to edit or remove key words for a result. \n \n" +
                                    itemName;

                    dialog.setPositiveButton(android.R.string.search_go, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mEditText.setText(itemName);
                        }
                    });

                    dialog.setNegativeButton(android.R.string.cancel, null).show();

                } else {
                    message +=
                            "Unfortunately, the database SelfImage utilizes for UPC information does not contain any information on this product. " +
                                    "We apologize for the inconvenience. ";

                    dialog.setNeutralButton(android.R.string.ok, null);
                }

                dialog.setMessage(message).show();

            }
        }).execute(upc);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null && scanResult.getContents() != null) {
            String upc = scanResult.getContents();
            searchForFoodItemsWithUPC(upc);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
