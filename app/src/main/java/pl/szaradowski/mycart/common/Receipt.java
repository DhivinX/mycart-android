/*
 * Created by Dominik Szaradowski on 24.05.19 11:47
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.common;

import android.content.Context;

import pl.szaradowski.mycart.R;

public class Receipt {
    private long id;
    private String name;
    private long time;
    private int cnt;
    private float val;

    public Receipt(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public float getVal() {
        return val;
    }

    public void setVal(float val) {
        this.val = val;
    }

    public String getTimeString(Context ctx){
        String tstr;

        if(RichDateUtils.isToday(time)) tstr = RichDateUtils.getHHMM(time);
        else if(RichDateUtils.isYesterday(time)) tstr = ctx.getString(R.string.yesterday);
        else if(RichDateUtils.isLast5day(time)) {
            tstr = ctx.getString(ctx.getResources().getIdentifier("day_sm_" + RichDateUtils.getDayOfWeek(time), "string", ctx.getPackageName()));
        }
        else if(RichDateUtils.isThisYear(time)) {
            tstr = RichDateUtils.getDayNumber(time) +" " + ctx.getString(ctx.getResources().getIdentifier("month_sm_" + RichDateUtils.getMonthNumber(time), "string", ctx.getPackageName()));
        }else{
            tstr = RichDateUtils.getDayNumber(time) + " " + ctx.getString(ctx.getResources().getIdentifier("month_sm_" + RichDateUtils.getMonthNumber(time), "string", ctx.getPackageName()))+ " " + RichDateUtils.getYear(time);
        }

        return tstr;
    }
}
