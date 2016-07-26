package com.j1adong.meizi;

import android.support.annotation.NonNull;

/**
 * Created by J1aDong on 16/7/22.
 */
public class GankDate {
    private int year;
    private int month;
    private int day;

    public GankDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "GankDate{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }

    public boolean compareTo(@NonNull GankDate gankDate) {
        if (gankDate.getYear() == year && gankDate.getMonth() == month && gankDate.getDay() == day) {
            return true;
        } else {
            return false;
        }
    }

    public String getYYYYMMdd() {
        String s = year + "年" + month + "月" + day + "日";
        return s;
    }
}
