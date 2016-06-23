package com.pluviostudios.selfimage.utilities;

import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Spectre on 5/11/2016.
 */
public class Utilities {

    private static Long currentNormalizedDate = null;

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

    public static String formatDateFromMillis(long startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(cal.getTime());
    }


}
