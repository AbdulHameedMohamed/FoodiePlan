package com.abdulhameed.foodieplan.utils;

import java.util.Calendar;

public class MyCalender {
    private static final String[] daysOfWeek = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    public static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String[] getDaysOfWeek() {
        return daysOfWeek;
    }
    public static String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            default:
                return "";
        }
    }
}
