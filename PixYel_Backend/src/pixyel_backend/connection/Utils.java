/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Josua Frank
 */
public class Utils {

    public static final double MAXWIDTH = 720.0;

    public static String compressImage(String asBase64) {
        try {
            byte[] byteArrayImage = java.util.Base64.getDecoder().decode(asBase64);
            InputStream in = new ByteArrayInputStream(byteArrayImage);
            BufferedImage image = ImageIO.read(in);
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            if (imageWidth > MAXWIDTH) {
                int newWidth = (int) ((MAXWIDTH / (double) imageWidth) * (double) imageWidth);
                int newHeight = (int) ((MAXWIDTH / (double) imageWidth) * (double) imageHeight);
                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
                Graphics2D g = resizedImage.createGraphics();
                g.drawImage(image, 0, 0, newWidth, newHeight, null);
                g.dispose();
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", bao);
                return java.util.Base64.getEncoder().encodeToString(bao.toByteArray());
            } else {
                return asBase64;
            }
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return asBase64;
    }

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
