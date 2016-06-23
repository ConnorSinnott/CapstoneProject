package com.pluviostudios.selfimage.utilities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pluviostudios.selfimage.R;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/20/2016.
 */
public class SimpleStringArrayDialog extends DialogFragment {

    public static final String REFERENCE_ID = "SimpleListDialog";
    public static final String EXTRA_LIST = "extra_list";
    public static final String EXTRA_POSITION = "extra_position";

    private View mRoot;
    private ListView mListView;

    public static SimpleStringArrayDialog createCategoryDialog(Fragment dialogResultTarget, int requestCode, ArrayList<String> data) {
        SimpleStringArrayDialog dialogFragmentCategoryPicker = new SimpleStringArrayDialog();
        dialogFragmentCategoryPicker.setTargetFragment(dialogResultTarget, requestCode);
        Bundle args = new Bundle();
        args.putStringArrayList(EXTRA_LIST, data);
        return dialogFragmentCategoryPicker;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_edit_categories, container, false);

        if (!getArguments().containsKey(EXTRA_LIST)) {
            throw new MissingExtraException(EXTRA_LIST);
        }

        mListView = (ListView) mRoot.findViewById(R.id.fragment_edit_categories_list_view);

        mListView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.fragment_edit_categories_list_item, getArguments().getStringArrayList(EXTRA_LIST)));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.putExtra(EXTRA_POSITION, position);

                SimpleStringArrayDialog.this.getTargetFragment().onActivityResult(
                        SimpleStringArrayDialog.this.getTargetRequestCode(),
                        0,
                        intent
                );

                SimpleStringArrayDialog.this.dismiss();

            }
        });

        return mRoot;
    }

}
