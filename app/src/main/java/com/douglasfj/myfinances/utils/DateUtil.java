package com.douglasfj.myfinances.utils;

import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    /**
     *
     * @return string in format Jan/00
     */
    public static String dateToSmallString(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String monthString = Month.of(calendar.get(Calendar.MONTH)+1).getDisplayName(TextStyle.SHORT, Locale.getDefault());
        String year = String.valueOf(Year.of(calendar.get(Calendar.YEAR)).getValue()).substring(2);
        return monthString+"/"+year;
    }
}
