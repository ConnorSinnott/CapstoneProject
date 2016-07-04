package com.pluviostudios.selfimage.utilities;

import android.content.Context;
import android.text.format.Time;

import com.pluviostudios.selfimage.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Spectre on 5/11/2016.
 */
public class DateUtils {

    private static Long currentNormalizedDate = null;

    // There will be an issue if the user is using this past midnight
    public static long getCurrentNormalizedDate() {
        if (currentNormalizedDate != null) {
            return currentNormalizedDate;
        } else {
            updateCurrentNormalizedDate();
            return currentNormalizedDate;
        }
    }

    public static void updateCurrentNormalizedDate() {
        currentNormalizedDate = normalizeDate(Calendar.getInstance().getTimeInMillis());
    }

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static boolean isTodaysDate(long date) {
        return date == getCurrentNormalizedDate();
    }

    public static String getSpecialFormattedDate(Context context, long date) {

        date = normalizeDate(date);
        int diff = Math.round((date - getCurrentNormalizedDate()) / 86400000);

        if (diff == 0) {
            return context.getString(R.string.date_today);
        } else if (diff == -1) {
            return context.getString(R.string.date_yesterday);
        } else if (diff >= -7) {
            SimpleDateFormat format = new SimpleDateFormat("EEEE");
            return format.format(date);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("MMM d");
            return format.format(date);
        }

    }


}
