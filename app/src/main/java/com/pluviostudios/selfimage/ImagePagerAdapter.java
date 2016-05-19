package com.pluviostudios.selfimage;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Spectre on 5/16/2016.
 */
public class ImagePagerAdapter extends CursorFragmentPagerAdapter {


    private View.OnClickListener mOnFABClickListener;
    private Context mContext;

    public ImagePagerAdapter(Context context, FragmentManager fm, Cursor cursor) {
        super(context, fm, cursor);
    }

    @Override
    public Fragment getItem(Context context, Cursor cursor) {
        DayCardFragment fragment = new DayCardFragment();

        fragment.setDate(cursor.getLong(1));
        if (cursor.isNull(2)) {
            fragment.setUri(null);
        } else {
            fragment.setUri(Uri.parse(cursor.getString(2)));
        }
        fragment.setOnFABClickListener(mOnFABClickListener);

        return fragment;
    }

    public void setOnFABClickListener(View.OnClickListener onFABClickListener) {
        mOnFABClickListener = onFABClickListener;
    }

    public static class DayCardFragment extends Fragment {

        private View mRoot;
        private View.OnClickListener mOnFABClickListener;
        private Uri mUri;
        private long mDate;

        public void setDate(long date) {
            mDate = date;
        }

        public void setUri(Uri uri) {
            mUri = uri;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mRoot = inflater.inflate(R.layout.fragment_day_card, container, false);

            boolean isCurrent = mDate == Utilities.getCurrentNormalizedDate();
            if (isCurrent) {
                FloatingActionButton fab = (FloatingActionButton) mRoot.findViewById(R.id.fragment_day_card_FAB);
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(mOnFABClickListener);
                fab.setImageResource((mUri == null) ?
                        R.drawable.ic_add_a_photo_white_24dp :
                        R.drawable.ic_photo_camera_white_24dp);
            }

            if (mUri != null) {
                ImageView imageView = (ImageView) mRoot.findViewById(R.id.fragment_day_card_imageview);
                imageView.setImageURI(mUri);
            }

            return mRoot;
        }

        public void setOnFABClickListener(View.OnClickListener onFABClickListener) {
            mOnFABClickListener = onFABClickListener;
        }

    }

}
