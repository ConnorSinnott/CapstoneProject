package com.pluviostudios.selfimage;

import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Spectre on 5/11/2016.
 */
public class Utilities {

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static long getCurrentNormalizedDate() {
        return normalizeDate(Calendar.getInstance().getTimeInMillis());
    }

    public static String formatDateFromMillis(long startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(cal.getTime());
    }


}
