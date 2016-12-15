/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI.apps;

import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.database.dataProcessing.Statistics;
import pixyel_backend.database.exceptions.FlagFailedExcpetion;
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.exceptions.UserNotFoundException;
import pixyel_backend.database.objects.Picture;
import pixyel_backend.database.objects.User;
import pixyel_backend.userinterface.Translations;
import pixyel_backend.userinterface.UIs.ConfirmationDialog;
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

    private int state = 0;//0 = nothing, 1 = newest, 2 = top, 3 = specialImage
    private int specialImage;
    VerticalLayout right = new VerticalLayout();

    public Layout getLayout() {
        GridLayout gridl = new GridLayout(2, 1);
        gridl.setSpacing(true);
        gridl.setMargin(true);

        VerticalLayout left = new VerticalLayout();
        left.setSpacing(true);
        left.setMargin(true);

        right.setSpacing(true);
        right.setMargin(true);
        right.setSizeUndefined();

        Button buttonNewest = new Button(Translations.get(Translations.SYSTEMMONITOR_NEWESTIMAGES));
        left.addComponent(buttonNewest);
        left.setComponentAlignment(buttonNewest, Alignment.MIDDLE_LEFT);
        buttonNewest.addClickListener((Button.ClickEvent ce) -> {
            state = 1;
            right.removeAllComponents();
            List<Picture> newestPictures = Picture.newestPictures(10, 1);
            newestPictures.forEach((Picture newestPicture) -> {
                addPicture(newestPicture, right);
            });
        });

        Button buttonTop = new Button(Translations.get(Translations.SYSTEMMONITOR_TOPIMAGE));
        left.addComponent(buttonTop);
        left.setComponentAlignment(buttonTop, Alignment.MIDDLE_LEFT);
        buttonTop.addClickListener((Button.ClickEvent ce) -> {
            state = 2;
            right.removeAllComponents();
            List<Picture> topPictures = Picture.getWorldwideTopPictures(1);
            topPictures.forEach((Picture topPicture) -> {
                addPicture(topPicture, right);
            });
        });

        HorizontalLayout textFieldAndButton = new HorizontalLayout();
        textFieldAndButton.setSpacing(true);
        TextField textFieldGet = new TextField();
        textFieldGet.setWidth(35, Unit.PIXELS);
        textFieldAndButton.addComponent(textFieldGet);
        Button buttonGet = new Button(Translations.get(Translations.SYSTEMMONITOR_SPECIALIMAGE));
        textFieldAndButton.addComponent(buttonGet);
        buttonGet.addClickListener((Button.ClickEvent ce) -> {
            String sID = textFieldGet.getValue();
            right.removeAllComponents();
            if (sID != null && !sID.isEmpty()) {
                state = 3;
                try {
                    specialImage = Integer.valueOf(sID);
                    Picture picture = Picture.getPictureById(Integer.valueOf(sID), 1);
                    addPicture(picture, right);
                } catch (PictureLoadException ex) {
                    Notification.show("Image not available", Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        left.addComponent(textFieldAndButton);
        left.setComponentAlignment(textFieldAndButton, Alignment.MIDDLE_LEFT);

        TextField statistic;
        Statistics.countBannedUsers();
        Statistics.countRegisteredUsers();
        Statistics.countUploadedPictures();
        gridl.addComponent(left, 0, 0);
        gridl.addComponent(right, 1, 0);
        //gridl.setColumnExpandRatio(0, );
        return gridl;
    }

    private void addPicture(Picture picture, VerticalLayout layout) {
        Image image = new Image();
        image.setSource(getImage(picture));
        layout.addComponent(image);
        HorizontalLayout options = new HorizontalLayout();
        options.setSpacing(true);
        Label votes = new Label("Id: " + picture.getId() + " Upvotes: " + picture.getUpvotes()
                + " Downvotes: " + picture.getDownvotes() + " Flags: " + picture.countFlags());
        votes.setStyleName(ValoTheme.LABEL_LARGE);
        options.addComponent(votes);
        String caption = "";
        try {
            if (!User.getUser(1).hasFlaggedPicture(picture.getId())) {
                caption = "Flag Item";//TODO
            } else {
                caption = "Unflag Item";
            }
        } catch (UserNotFoundException | UserCreationException | FlagFailedExcpetion ex) {
            Logger.getLogger(OnlineMonitorWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        Button flag = new Button(caption);
        flag.setStyleName(ValoTheme.BUTTON_PRIMARY);
        flag.addClickListener(cev -> {
            try {
                if (User.getUser(1).hasFlaggedPicture(picture.getId())) {
                    flag.setCaption("Flag Item");
                    Picture.removeFlagForUser(picture.getId(), 1);
                    //options.removeComponent(votes);
                    votes.setValue("Id: " + picture.getId() + " Upvotes: " + picture.getUpvotes()
                            + " Downvotes: " + picture.getDownvotes() + " Flags: " + picture.countFlags());
                    //votes.setStyleName(ValoTheme.LABEL_LARGE);
                    //options.addComponent(votes, 0);
                    //markAsDirtyRecursive();
                } else {
                    flag.setCaption("Unflag Item");
                    Picture.flagPicture(1, picture.getId());
                    //options.removeComponent(votes);
                    votes.setValue("Id: " + picture.getId() + " Upvotes: " + picture.getUpvotes()
                            + " Downvotes: " + picture.getDownvotes() + " Flags: " + picture.countFlags());
                    //votes.setStyleName(ValoTheme.LABEL_LARGE);
                    //options.addComponent(votes, 0);
                    //markAsDirtyRecursive();
                }
            } catch (UserNotFoundException | UserCreationException | FlagFailedExcpetion ex) {
                Log.logError("Could not flag item: " + ex, OnlineMonitorWindow.class);
            }
        });
        options.addComponent(flag);//TODO
        Button delete = new Button("DELETE");
        delete.setStyleName(ValoTheme.BUTTON_DANGER);
        delete.addClickListener(clev
                -> ConfirmationDialog.show("Really delete image?",
                        () -> {
                            Picture.deletePicture(picture.getId());
                            switch (state) {
                                case 1:
                                    right.removeAllComponents();
                                    List<Picture> newestPictures = Picture.newestPictures(10, 1);
                                    newestPictures.forEach((Picture newestPicture) -> {
                                        addPicture(newestPicture, right);
                                    });
                                    break;
                                case 2:
                                    right.removeAllComponents();
                                    List<Picture> topPictures = Picture.getWorldwideTopPictures(1);
                                    topPictures.forEach((Picture topPicture) -> {
                                        addPicture(topPicture, right);
                                    });
                                    break;
                                case 3:
                                    right.removeAllComponents();
                                    break;
                            }
                        },
                        () -> {
                        }));//TODO
        options.addComponent(delete);
        layout.addComponent(options);
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
