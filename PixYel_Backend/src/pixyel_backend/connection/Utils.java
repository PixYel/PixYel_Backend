/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Josua Frank
 */
public class Utils {

    public static String getDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String getDate(Date date, Date time) {
        return getDate(mergeDates(date, time));
    }

    public static Date mergeDates(Date date, Date time) {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);

        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTime(time);

        Calendar calendarDateTime = Calendar.getInstance();
        calendarDateTime.set(Calendar.DAY_OF_MONTH, calendarDate.get(Calendar.DAY_OF_MONTH));
        calendarDateTime.set(Calendar.MONTH, calendarDate.get(Calendar.MONTH));
        calendarDateTime.set(Calendar.YEAR, calendarDate.get(Calendar.YEAR));
        calendarDateTime.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
        calendarDateTime.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
        calendarDateTime.set(Calendar.SECOND, calendarTime.get(Calendar.SECOND));
        
        return calendarDateTime.getTime();
    }

}
