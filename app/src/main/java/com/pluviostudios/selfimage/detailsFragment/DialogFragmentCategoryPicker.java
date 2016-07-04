package com.pluviostudios.selfimage.detailsFragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pluviostudios.selfimage.R;
import com.pluviostudios.selfimage.data.database.DatabaseContract;

/**
 * Created by Spectre on 6/12/2016.
 */
public class DialogFragmentCategoryPicker extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String REFERENCE_ID = "DialogFragmentCatPicker";
    public static final String EXTRA_CATEGORY = "extra_category";

    private View mRoot;
    private ListView mListView;

    public static DialogFragmentCategoryPicker createCategoryDialog(Fragment fragment, int requestCode) {
        DialogFragmentCategoryPicker dialogFragmentCategoryPicker = new DialogFragmentCategoryPicker();
        dialogFragmentCategoryPicker.setTargetFragment(fragment, requestCode);
        return dialogFragmentCategoryPicker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_edit_categories, container, false);
        mListView = (ListView) mRoot.findViewById(R.id.fragment_edit_categories_list_view);
        getLoaderManager().initLoader(0, null, this);
        return mRoot;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                DatabaseContract.CategoryEntry.CONTENT_URI,
                new String[]{
                        DatabaseContract.CategoryEntry._ID,
                        DatabaseContract.CategoryEntry.CATEGORY_INDEX_COL,
                        DatabaseContract.CategoryEntry.CATEGORY_NAME_COL
                },
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null)
            return;

        mListView.setAdapter(new CursorAdapter(getContext(), data) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.fragment_edit_categories_list_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ((TextView) view.findViewById(R.id.fragment_edit_categories_list_item_text_view)).setText(cursor.getString(2));
                view.findViewById(R.id.fragment_edit_categories_list_item_ll).setOnClickListener(new View.OnClickListener() {

                    private int category;

                    public View.OnClickListener setCategory(int category) {
                        this.category = category;
                        return this;
                    }

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_CATEGORY, category);

                        DialogFragmentCategoryPicker.this.getTargetFragment().onActivityResult(
                                DialogFragmentCategoryPicker.this.getTargetRequestCode(),
                                0,
                                intent
                        );

                        DialogFragmentCategoryPicker.this.dismiss();
                    }

                }.setCategory(cursor.getInt(1)));
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((CursorAdapter) mListView.getAdapter()).swapCursor(null);
    }
}
