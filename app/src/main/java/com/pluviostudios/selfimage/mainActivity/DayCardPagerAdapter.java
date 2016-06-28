package com.pluviostudios.selfimage.mainActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pluviostudios.selfimage.data.dataContainers.date.DateItem;
import com.pluviostudios.selfimage.data.database.DatabaseContract;

import java.util.ArrayList;

/**
 * Created by Spectre on 6/25/2016.
 */
public class DayCardPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<DateItem> mData;

    public DayCardPagerAdapter(FragmentManager fm, ArrayList<DateItem> data) {
        super(fm);
        mData = data;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        DateItem currItem = mData.get(position);
        args.putString(DatabaseContract.DateEntry.DATE_COL, String.valueOf(currItem.date));
        args.putString(DatabaseContract.DateEntry.IMAGE_DIRECTORY_COL, String.valueOf(currItem.img_dir));
        DayCardFragment fragment = new DayCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

}
