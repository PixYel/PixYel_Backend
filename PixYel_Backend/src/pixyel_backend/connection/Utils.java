/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
            byte[] imageByteArray = java.util.Base64.getDecoder().decode(asBase64);

            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageByteArray));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            BufferedImage resizeImage = resizeImage(originalImage, type);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizeImage, "jpg", baos);
            baos.flush();
            imageByteArray = baos.toByteArray();
            baos.close();

            return java.util.Base64.getEncoder().encodeToString(imageByteArray);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return asBase64;
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type) {
        int originalWidth = originalImage.getWidth();
        if ((originalWidth + 25) <= MAXWIDTH) {
            return originalImage;
        }
        int originalHeight = originalImage.getHeight();
        int resizedWidth = (int) (((double) MAXWIDTH / (double) originalWidth) * (double) originalWidth);
        int resizedHeight = (int) (((double) MAXWIDTH / (double) originalWidth) * (double) originalHeight);

        BufferedImage resizedImage = new BufferedImage(resizedWidth, resizedHeight, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, resizedWidth, resizedHeight, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
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
