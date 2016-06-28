package com.pluviostudios.selfimage.utilities;

import android.text.format.Time;
import android.util.Log;

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

    public static String getSpecialFormattedDate(long date) {

        date = normalizeDate(date);
        int diff = Math.round((date - getCurrentNormalizedDate()) / 86400000);

        Log.d("TEST", String.valueOf(diff));

        if (diff == 0) {
            return "Today";
        } else if (diff == -1) {
            return "Yesterday";
        } else if (diff >= -7) {
            SimpleDateFormat format = new SimpleDateFormat("EEEE");
            return format.format(date);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("MMM d");
            return format.format(date);
        }

    }


}
