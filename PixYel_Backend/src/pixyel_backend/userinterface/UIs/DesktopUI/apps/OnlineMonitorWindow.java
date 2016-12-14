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

        VerticalLayout left = new VerticalLayout();
        VerticalLayout right = new VerticalLayout();

        Button buttonNewest = new Button("NEW");
        left.addComponent(buttonNewest);
        buttonNewest.addClickListener((Button.ClickEvent ce) -> {
            right.removeAllComponents();
            List<Picture> newestPictures = Picture.newestPictures(10, 1);
            newestPictures.forEach((Picture newestPicture) -> {
                Image image = new Image();
                image.setSource(getImage(newestPicture));
                right.addComponent(image);
                Label votes = new Label("Upvotes: " + newestPicture.getUpvotes() + " Downvotes: " + newestPicture.getDownvotes());
                right.addComponent(votes);
            });
        });

        Button buttonTop = new Button("TODOTOP");
        left.addComponent(buttonTop);
        buttonTop.addClickListener((Button.ClickEvent ce) -> {

        });

        HorizontalLayout textFieldAndButton = new HorizontalLayout();
        TextField textFieldGet = new TextField();
        textFieldAndButton.addComponent(textFieldGet);
        Button buttonGet = new Button("TODOGET");
        textFieldAndButton.addComponent(buttonGet);
        buttonGet.addClickListener((Button.ClickEvent ce) -> {

        });

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
