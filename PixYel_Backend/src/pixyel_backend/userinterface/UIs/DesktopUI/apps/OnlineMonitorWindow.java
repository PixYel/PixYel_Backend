/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI.apps;

import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.objects.Coordinate;
import pixyel_backend.database.objects.Picture;
import pixyel_backend.userinterface.Translations;
import pixyel_backend.userinterface.ressources.Ressources;

/**
 *
 * @author Josua Frank
 */
public class OnlineMonitorWindow extends Window {

    public static void show(Runnable onClose) {
        new OnlineMonitorWindow(onClose);
    }

    public OnlineMonitorWindow(Runnable onClose) {
        addCloseListener((Window.CloseEvent ce) -> {
            onClose.run();
        });
        Page.getCurrent().addBrowserWindowResizeListener((s) -> onResized());

        setImmediate(true);
        setContent(getLayout());
        setCaption(" " + Translations.get(Translations.DESKTOP_ONLINE_MONITOR));

        try {
            setIcon(new FileResource(Ressources.getRessource("desktop_system_monitor_icon_small.png")));
        } catch (Ressources.RessourceNotFoundException ex) {
            Log.logWarning("Could not set the Console icon: " + ex, ConsoleWindow.class);
        }

        setDraggable(true);
        center();
        int WIDTH = (int) (0.5 * UI.getCurrent().getPage().getBrowserWindowWidth());
        int HIGHT = (int) (0.5 * UI.getCurrent().getPage().getBrowserWindowHeight());
        setWidth(WIDTH, Sizeable.Unit.PIXELS);
        setHeight(HIGHT, Sizeable.Unit.PIXELS);
        UI.getCurrent().addWindow(this);
    }

    public void onResized() {
        if (isAttached()) {
            int WIDTH = (int) (0.5 * UI.getCurrent().getPage().getBrowserWindowWidth());
            int HIGHT = (int) (0.5 * UI.getCurrent().getPage().getBrowserWindowHeight());
            setWidth(WIDTH, Sizeable.Unit.PIXELS);
            setHeight(HIGHT, Sizeable.Unit.PIXELS);
        }
    }

    public GridLayout getLayout() {
        GridLayout gridl = new GridLayout(2, 1);
        gridl.setSpacing(true);

        VerticalLayout left = new VerticalLayout();
        left.setSpacing(true);
        VerticalLayout right = new VerticalLayout();
        right.setSpacing(true);

        Button buttonNewest = new Button(Translations.get(Translations.SYSTEMMONITOR_NEWESTIMAGES));
        left.addComponent(buttonNewest);
        left.setComponentAlignment(buttonNewest, Alignment.MIDDLE_LEFT);
        buttonNewest.addClickListener((Button.ClickEvent ce) -> {
            right.removeAllComponents();
            List<Picture> newestPictures = Picture.newestPictures(10, 1);
            newestPictures.forEach((Picture newestPicture) -> {
                Image image = new Image();
                image.setSource(getImage(newestPicture));
                right.addComponent(image);
                Label votes = new Label("Id: " + newestPicture.getId() + " Upvotes: " + newestPicture.getUpvotes() + " Downvotes: " + newestPicture.getDownvotes());
                right.addComponent(votes);
            });
        });

        Button buttonTop = new Button(Translations.get(Translations.SYSTEMMONITOR_TOPIMAGE));
        left.addComponent(buttonTop);
        left.setComponentAlignment(buttonTop, Alignment.MIDDLE_LEFT);
        buttonTop.addClickListener((Button.ClickEvent ce) -> {
            right.removeAllComponents();
            List<Picture> topPictures = Picture.getWorldwideTopPictures(0);
            topPictures.forEach((Picture topPicture) -> {
                Image image = new Image();
                image.setSource(getImage(topPicture));
                right.addComponent(image);
                Label votes = new Label("Id: " + topPicture.getId() + " Upvotes: " + topPicture.getUpvotes() + " Downvotes: " + topPicture.getDownvotes());
                right.addComponent(votes);
            });
        });

        HorizontalLayout textFieldAndButton = new HorizontalLayout();
        //textFieldAndButton.setSpacing(true);
        TextField textFieldGet = new TextField();
        textFieldAndButton.addComponent(textFieldGet);
        Button buttonGet = new Button(Translations.get(Translations.SYSTEMMONITOR_SPECIALIMAGE));
        textFieldAndButton.addComponent(buttonGet);
        buttonGet.addClickListener((Button.ClickEvent ce) -> {
            String sID = textFieldGet.getValue();
            if (sID != null && !sID.isEmpty()) {
                try {
                    Picture picture = Picture.getPictureById(Integer.valueOf(sID), 0);
                    Image image = new Image();
                    image.setSource(getImage(picture));
                    right.addComponent(image);
                    Label votes = new Label("Id: " + picture.getId() + " Upvotes: " + picture.getUpvotes() + " Downvotes: " + picture.getDownvotes());
                    right.addComponent(votes);
                } catch (PictureLoadException ex) {
                    Logger.getLogger(OnlineMonitorWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        left.addComponent(textFieldAndButton);
        left.setComponentAlignment(textFieldAndButton, Alignment.MIDDLE_LEFT);

        gridl.addComponent(left, 0, 0);
        gridl.addComponent(right, 1, 0);
        return gridl;
    }

    public FileResource getImage(Picture picture) {
        byte[] decoded = java.util.Base64.getDecoder().decode(picture.getData());
        File imageDir = new File(System.getProperty("user.home") + "\\Desktop\\PixYel\\Images");
        if (!imageDir.exists()) {
            if (!imageDir.mkdirs()) {
                Log.logError("Could not create Imagedirectory", OnlineMonitorWindow.class);
                return null;
            }
        }
        File image = new File(imageDir.getAbsolutePath() + picture.getId() + ".jpg");
        if (image.exists()) {
            return new FileResource(image);
        }
        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(image));
            bos.write(decoded);
            bos.flush();
            bos.close();
            return new FileResource(image);
        } catch (IOException e) {
            Log.logError("Could not write Imagefile: " + e, OnlineMonitorWindow.class);
        }
        return null;
    }

}
