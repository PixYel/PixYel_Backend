/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author i01frajos445
 */
public class Utils {

    public static String getDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    public static  String getDate(Date date, Date time) {
        return getDate(new Date(
                date.getYear(), date.getMonth(), date.getDay(),
                time.getHours(), time.getMinutes(), time.getSeconds()
        ));
    }

}
