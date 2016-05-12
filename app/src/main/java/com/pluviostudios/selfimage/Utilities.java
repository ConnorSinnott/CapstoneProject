package com.pluviostudios.selfimage;

import android.text.format.Time;

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

}
