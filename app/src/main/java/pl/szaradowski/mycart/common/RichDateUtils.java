/*
 * Created by Dominik Szaradowski on 26.05.19 00:23
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.common;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RichDateUtils{
    public static boolean isToday(long time) {
        return DateUtils.isToday(time);
    }

    public static boolean isYesterday(long time) {
        return DateUtils.isToday(time + DateUtils.DAY_IN_MILLIS);
    }

    public static boolean isLast5day(long time){
        return DateUtils.isToday(time + (DateUtils.DAY_IN_MILLIS * 4)) ||
                DateUtils.isToday(time + (DateUtils.DAY_IN_MILLIS * 3)) ||
                DateUtils.isToday(time + (DateUtils.DAY_IN_MILLIS * 2)) ||
                DateUtils.isToday(time + DateUtils.DAY_IN_MILLIS);
    }

    public static boolean isThisYear(long time){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        Calendar c2 = Calendar.getInstance();

        return c2.get(Calendar.YEAR) == c.get(Calendar.YEAR);
    }

    public static int getDayOfWeek(long time){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static int getDayNumber(long time){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonthNumber(long time){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        return c.get(Calendar.MONTH) + 1;
    }

    public static int getYear(long time){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        return c.get(Calendar.YEAR);
    }

    public static String getHHMM(long time){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Utils.locale);

            return dateFormat.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
