package com.tronography.locationchat.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by jonathancolon on 8/2/17.
 */

public class DateUtils {

    public static StringBuilder dateFormatter(long timeInMilli) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(timeInMilli);
        int month = (calendar.get(Calendar.MONTH)) + 1;
        int year = (calendar.get(Calendar.YEAR));
        int dayOfMonth = (calendar.get(Calendar.DAY_OF_MONTH));
        int hourOfDay = (calendar.get(Calendar.HOUR));
        int minute = (calendar.get(Calendar.MINUTE));
        int isAMorPM = (calendar.get(Calendar.AM_PM));

        String hour = hourOfDay == 0 ? "12" : String.valueOf(hourOfDay);

        String minutes;
        switch (minute) {
            case 0:
                minutes = "00";
                break;
            case 1:
                minutes = "01";
                break;
            case 2:
                minutes = "02";
                break;
            case 3:
                minutes = "03";
                break;
            case 4:
                minutes = "04";
                break;
            case 5:
                minutes = "05";
                break;
            case 6:
                minutes = "06";
                break;
            case 7:
                minutes = "07";
                break;
            case 8:
                minutes = "08";
                break;
            case 9:
                minutes = "09";
                break;
            default:
                minutes = String.valueOf(minute);
        }
        String amPm = isAMorPM == 0 ? "AM" : "PM";

        StringBuilder formattedDate = new StringBuilder()
                .append(month)
                .append("/")
                .append(dayOfMonth)
                .append("/")
                .append(year)
                .append(" ")
                .append(hour)
                .append(":")
                .append(minutes)
                .append(" ")
                .append(amPm);

        return formattedDate;
    }
}
